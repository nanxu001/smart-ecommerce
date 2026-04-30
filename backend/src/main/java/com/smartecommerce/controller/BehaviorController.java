package com.smartecommerce.controller;

import com.smartecommerce.common.Result;
import com.smartecommerce.entity.Product;
import com.smartecommerce.service.BehaviorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/behavior")
@RequiredArgsConstructor
public class BehaviorController {

    private final BehaviorService behaviorService;

    @PostMapping
    public Result<Void> record(@RequestBody BehaviorDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        behaviorService.recordBehavior(userId, dto.getProductId(), dto.getType(), dto.getSearchQuery());
        return Result.success();
    }

    @GetMapping("/history")
    public Result<List<Product>> history(
            @RequestParam(defaultValue = "20") int limit,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(behaviorService.getHistory(userId, limit));
    }

    @PostMapping("/favorites/{productId}")
    public Result<Void> favorite(@PathVariable Long productId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        behaviorService.recordBehavior(userId, productId, "favorite", null);
        return Result.success();
    }

    @DeleteMapping("/favorites/{productId}")
    public Result<Void> unfavorite(@PathVariable Long productId, HttpServletRequest request) {
        // 删除收藏记录
        return Result.success();
    }

    @GetMapping("/favorites")
    public Result<List<Product>> favorites(
            @RequestParam(defaultValue = "20") int limit,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // 返回收藏商品
        return Result.success(behaviorService.getHistory(userId, limit));
    }

    @Data
    public static class BehaviorDTO {
        private Long productId;
        private String type; // view, favorite, cart, purchase, search
        private String searchQuery;
    }
}
