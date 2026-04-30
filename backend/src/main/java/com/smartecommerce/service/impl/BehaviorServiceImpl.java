package com.smartecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartecommerce.entity.Product;
import com.smartecommerce.entity.UserBehavior;
import com.smartecommerce.mapper.ProductMapper;
import com.smartecommerce.mapper.UserBehaviorMapper;
import com.smartecommerce.service.BehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BehaviorServiceImpl extends ServiceImpl<UserBehaviorMapper, UserBehavior> implements BehaviorService {

    private final ProductMapper productMapper;

    @Override
    public void recordBehavior(Long userId, Long productId, String type, String searchQuery) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setProductId(productId);
        behavior.setType(type);
        behavior.setSearchQuery(searchQuery);
        save(behavior);

        // 如果是浏览，增加商品浏览量
        if ("view".equals(type) && productId != null) {
            Product product = productMapper.selectById(productId);
            if (product != null) {
                product.setViews(product.getViews() + 1);
                productMapper.updateById(product);
            }
        }
    }

    @Override
    public List<Product> getHistory(Long userId, int limit) {
        // 获取最近浏览的商品
        List<Long> productIds = list(new LambdaQueryWrapper<UserBehavior>()
                .eq(UserBehavior::getUserId, userId)
                .eq(UserBehavior::getType, "view")
                .orderByDesc(UserBehavior::getCreatedAt)
                .last("LIMIT " + limit))
                .stream()
                .map(UserBehavior::getProductId)
                .distinct()
                .collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return List.of();
        }

        return productMapper.selectBatchIds(productIds);
    }
}
