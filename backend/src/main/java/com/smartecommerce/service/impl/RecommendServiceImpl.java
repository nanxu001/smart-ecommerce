package com.smartecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartecommerce.entity.*;
import com.smartecommerce.mapper.*;
import com.smartecommerce.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendServiceImpl extends ServiceImpl<RecommendationMapper, Recommendation> implements RecommendService {

    private final ProductMapper productMapper;
    private final UserBehaviorMapper userBehaviorMapper;
    private final FavoriteMapper favoriteMapper;
    private final UserProfileMapper userProfileMapper;

    @Override
    public List<Product> guessYouLike(Long userId, int limit) {
        // 协同过滤推荐
        // 1. 获取当前用户的行为商品
        List<UserBehavior> myBehaviors = userBehaviorMapper.selectList(
                new LambdaQueryWrapper<UserBehavior>().eq(UserBehavior::getUserId, userId));
        
        if (myBehaviors.isEmpty()) {
            // 新用户：返回热门商品
            return productMapper.selectHotProducts(limit);
        }

        Map<Long, Integer> myProducts = new HashMap<>();
        for (UserBehavior b : myBehaviors) {
            int weight = "purchase".equals(b.getType()) ? 3 : 
                         "favorite".equals(b.getType()) || "cart".equals(b.getType()) ? 2 : 1;
            myProducts.merge(b.getProductId(), weight, Integer::sum);
        }

        // 2. 找相似用户（喜欢相同商品的人）
        List<Long> similarUserIds = findSimilarUsers(myProducts.keySet(), userId, 10);

        // 3. 收集相似用户喜欢但当前用户没看过的商品
        Map<Long, Integer> candidateScores = new HashMap<>();
        for (Long similarUserId : similarUserIds) {
            List<UserBehavior> theirBehaviors = userBehaviorMapper.selectList(
                    new LambdaQueryWrapper<UserBehavior>().eq(UserBehavior::getUserId, similarUserId));
            
            for (UserBehavior b : theirBehaviors) {
                if (!myProducts.containsKey(b.getProductId())) {
                    int weight = "purchase".equals(b.getType()) ? 3 : 
                                 "favorite".equals(b.getType()) || "cart".equals(b.getType()) ? 2 : 1;
                    candidateScores.merge(b.getProductId(), weight, Integer::sum);
                }
            }
        }

        // 4. 排序取 top-N
        List<Long> topIds = candidateScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (topIds.isEmpty()) {
            return productMapper.selectHotProducts(limit);
        }

        List<Product> products = productMapper.selectBatchIds(topIds);
        products.sort(Comparator.comparing(p -> topIds.indexOf(p.getId())));
        return products;
    }

    @Override
    public List<Product> getHotProducts(int limit) {
        return productMapper.selectHotProducts(limit);
    }

    @Override
    public List<Product> getRelatedProducts(Long productId, int limit) {
        Product product = productMapper.selectById(productId);
        if (product == null) return List.of();

        // 基于同分类 + 同标签的相关性推荐
        List<Product> related = productMapper.selectRelatedByCategory(
                product.getCategoryId(), productId, limit);

        // 如果同分类不够，补充同标签的
        if (related.size() < limit && product.getTags() != null) {
            String tags = product.getTags();
            List<Product> byTag = productMapper.selectByTags(tags, productId, limit - related.size());
            related.addAll(byTag);
        }

        return related.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<Product> semanticRecommend(Long userId, String query, int limit) {
        // 语义推荐：解析自然语言查询，做关键词匹配 + 价格过滤
        if (query == null || query.trim().isEmpty()) {
            return productMapper.selectHotProducts(limit);
        }

        // --- 1. 提取预算约束 ---
        BigDecimal maxPrice = null;
        // 匹配 "500以内", "不超过1000", "预算800", "500以下", "小于300", "低于200元"
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(
                "(?:预算|不超过|小于|低于|以内|以下)[:\\s]*(\\d+)")
                .matcher(query);
        if (m.find()) {
            maxPrice = new BigDecimal(m.group(1));
        }

        // --- 2. 提取送礼意图，扩展关键词 ---
        Set<String> keywordSet = new HashSet<>();
        String[] keywords = query.toLowerCase()
                .split("[\\s，,。、！!？?；;]+");
        for (String kw : keywords) {
            String trimmed = kw.trim();
            if (!trimmed.isEmpty() && !trimmed.matches(".*\\d.*") &&
                    !trimmed.equals("预算") && !trimmed.equals("以内") &&
                    !trimmed.equals("以下") && !trimmed.equals("不超过")) {
                keywordSet.add(trimmed);
            }
        }

        // 送礼场景关键词扩展
        boolean isGift = query.contains("礼物") || query.contains("送") ||
                query.contains("女友") || query.contains("男友") ||
                query.contains("生日") || query.contains("情人节") ||
                query.contains("纪念日") || query.contains("圣诞") ||
                query.contains("新年");
        if (isGift) {
            // 常见礼物品类关键词
            keywordSet.addAll(Arrays.asList("耳机", "手表", "音箱", "护肤",
                    "香水", "口红", "手链", "项链", "杯子", "巧克力",
                    "花", "抱枕", "钱包", "围巾", "唇膏", "蓝牙",
                    "降噪", "无线"));
        }

        // 扩展关键词：基于用户画像
        UserProfile profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId));
        if (profile != null && profile.getPreferredCategories() != null) {
            String cats = profile.getPreferredCategories().replaceAll("[\\[\\]\"]", "");
            if (!cats.trim().isEmpty()) {
                keywordSet.addAll(Arrays.asList(cats.split(",")));
            }
        }

        // --- 3. 查询所有上架商品，计算匹配分数 + 价格过滤 ---
        List<Product> allProducts = productMapper.selectList(
                new LambdaQueryWrapper<Product>().eq(Product::getStatus, 1));

        List<Map.Entry<Product, Integer>> scored = new ArrayList<>();
        for (Product p : allProducts) {
            // 价格过滤
            if (maxPrice != null && p.getPrice() != null && p.getPrice().compareTo(maxPrice) > 0) {
                continue;
            }

            int score = 0;
            String searchable = (p.getName() + " " + p.getDescription() + " " +
                    (p.getTags() != null ? p.getTags() : "")).toLowerCase();

            for (String kw : keywordSet) {
                if (searchable.contains(kw)) {
                    score += 10;
                }
                if (p.getName() != null && p.getName().toLowerCase().contains(kw)) {
                    score += 20;
                }
                if (p.getTags() != null && p.getTags().toLowerCase().contains(kw)) {
                    score += 15;
                }
            }

            if (score > 0) {
                scored.add(new AbstractMap.SimpleEntry<>(p, score));
            }
        }

        // --- 4. 排序取 top-N，分数相同的按销量排 ---
        List<Product> result = scored.stream()
                .sorted((a, b) -> {
                    int cmp = b.getValue().compareTo(a.getValue());
                    if (cmp == 0) {
                        return Integer.compare(b.getKey().getSales(), a.getKey().getSales());
                    }
                    return cmp;
                })
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 如果语义匹配结果为空，返回热门商品
        if (result.isEmpty()) {
            return productMapper.selectHotProducts(limit);
        }

        return result;
    }

    @Override
    public List<String> getSearchHistory(Long userId) {
        List<UserBehavior> behaviors = userBehaviorMapper.selectList(
                new LambdaQueryWrapper<UserBehavior>()
                        .eq(UserBehavior::getUserId, userId)
                        .eq(UserBehavior::getType, "search")
                        .isNotNull(UserBehavior::getSearchQuery)
                        .orderByDesc(UserBehavior::getCreatedAt)
                        .last("LIMIT 20"));

        return behaviors.stream()
                .map(UserBehavior::getSearchQuery)
                .filter(q -> q != null && !q.trim().isEmpty())
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * 找相似用户：基于共同交互商品计算 Jaccard 相似度
     */
    private List<Long> findSimilarUsers(Set<Long> myProductIds, Long myUserId, int topK) {
        // 获取所有与当前用户有共同商品交互的用户
        List<UserBehavior> allBehaviors = userBehaviorMapper.selectList(
                new LambdaQueryWrapper<UserBehavior>().ne(UserBehavior::getUserId, myUserId));

        Map<Long, Set<Long>> userProducts = new HashMap<>();
        for (UserBehavior b : allBehaviors) {
            userProducts.computeIfAbsent(b.getUserId(), k -> new HashSet<>())
                    .add(b.getProductId());
        }

        List<Map.Entry<Long, Double>> similarities = new ArrayList<>();
        for (Map.Entry<Long, Set<Long>> entry : userProducts.entrySet()) {
            Set<Long> theirProducts = entry.getValue();
            // Jaccard 相似度
            Set<Long> intersection = new HashSet<>(myProductIds);
            intersection.retainAll(theirProducts);
            Set<Long> union = new HashSet<>(myProductIds);
            union.addAll(theirProducts);
            
            if (union.isEmpty()) continue;
            
            double similarity = (double) intersection.size() / union.size();
            if (similarity > 0) {
                similarities.add(new AbstractMap.SimpleEntry<>(entry.getKey(), similarity));
            }
        }

        return similarities.stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topK)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
