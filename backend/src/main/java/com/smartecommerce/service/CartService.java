package com.smartecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartecommerce.entity.CartItem;

import java.util.List;

public interface CartService extends IService<CartItem> {
    List<CartItem> getCart(Long userId);
    void addToCart(Long userId, Long productId, int quantity);
    void updateQuantity(Long userId, Long productId, int quantity);
    void removeFromCart(Long userId, Long productId);
    void clearCart(Long userId);
}
