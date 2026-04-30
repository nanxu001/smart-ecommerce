package com.smartecommerce.controller;

import com.smartecommerce.common.Result;
import com.smartecommerce.entity.CartItem;
import com.smartecommerce.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Result<List<CartItem>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(cartService.getCart(userId));
    }

    @PostMapping("/{productId}")
    public Result<Void> add(@PathVariable Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.addToCart(userId, productId, quantity);
        return Result.success();
    }

    @PutMapping("/{productId}")
    public Result<Void> update(@PathVariable Long productId,
                               @RequestParam int quantity,
                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.updateQuantity(userId, productId, quantity);
        return Result.success();
    }

    @DeleteMapping("/{productId}")
    public Result<Void> remove(@PathVariable Long productId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.removeFromCart(userId, productId);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> clear(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.clearCart(userId);
        return Result.success();
    }
}
