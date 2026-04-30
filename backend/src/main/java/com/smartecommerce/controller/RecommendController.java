package com.smartecommerce.controller;

import com.smartecommerce.common.Result;
import com.smartecommerce.entity.Product;
import com.smartecommerce.service.RecommendService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    /**
     * 猜你喜欢 — 协同过滤推荐
     */
    @GetMapping("/guess")
    public Result<List<Product>> guessYouLike(
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(recommendService.guessYouLike(userId, limit));
    }

    /**
     * 热门推荐
     */
    @GetMapping("/hot")
    public Result<List<Product>> hot(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(recommendService.getHotProducts(limit));
    }

    /**
     * 相关推荐 — 基于商品相似度
     */
    @GetMapping("/related/{productId}")
    public Result<List<Product>> related(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "6") int limit) {
        return Result.success(recommendService.getRelatedProducts(productId, limit));
    }

    /**
     * 语义推荐 — 自然语言查询
     * 例如："帮我挑适合送女友的生日礼物，预算500以内"
     */
    @PostMapping("/semantic")
    public Result<List<Product>> semantic(
            @RequestBody SemanticQueryDTO dto,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(recommendService.semanticRecommend(
                userId, dto.getQuery(), dto.getLimit() != null ? dto.getLimit() : 10));
    }

    /**
     * 搜索历史推荐
     */
    @GetMapping("/search-history")
    public Result<List<String>> searchHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(recommendService.getSearchHistory(userId));
    }

    @Data
    public static class SemanticQueryDTO {
        private String query;
        private Integer limit;
    }
}
