package com.smartecommerce.service;

import com.smartecommerce.entity.Product;

import java.util.List;

/**
 * AI 推荐引擎服务
 * 包含协同过滤、语义推荐、热门推荐、关联推荐
 */
public interface RecommendService {
    /**
     * 猜你喜欢 - 协同过滤推荐
     */
    List<Product> guessYouLike(Long userId, int limit);

    /**
     * 热门推荐 - 基于销量和浏览量加权排序
     */
    List<Product> getHotProducts(int limit);

    /**
     * 相关推荐 - 基于同分类+同标签共现分析
     */
    List<Product> getRelatedProducts(Long productId, int limit);

    /**
     * 语义推荐 - 自然语言查询匹配
     */
    List<Product> semanticRecommend(Long userId, String query, int limit);

    /**
     * 搜索历史推荐
     */
    List<String> getSearchHistory(Long userId);
}
