package com.smartecommerce.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartecommerce.common.Result;
import com.smartecommerce.entity.Favorite;
import com.smartecommerce.entity.Product;
import com.smartecommerce.mapper.FavoriteMapper;
import com.smartecommerce.mapper.ProductMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;

    @PostMapping("/{productId}")
    public Result<Void> add(@PathVariable Long productId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Favorite existing = favoriteMapper.selectOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getProductId, productId));
        if (existing != null) {
            return Result.success(); // 已收藏，不重复
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteMapper.insert(favorite);
        return Result.success();
    }

    @DeleteMapping("/{productId}")
    public Result<Void> remove(@PathVariable Long productId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));
        return Result.success();
    }

    @GetMapping
    public Result<List<Product>> list(
            @RequestParam(defaultValue = "20") int limit,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Favorite> favorites = favoriteMapper.selectList(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreatedAt)
                        .last("LIMIT " + limit));
        
        List<Long> productIds = favorites.stream()
                .map(Favorite::getProductId)
                .collect(Collectors.toList());
        
        if (productIds.isEmpty()) {
            return Result.success(List.of());
        }
        
        return Result.success(productMapper.selectBatchIds(productIds));
    }
}
