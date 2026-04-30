package com.smartecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartecommerce.entity.CartItem;
import com.smartecommerce.entity.Product;
import com.smartecommerce.mapper.CartItemMapper;
import com.smartecommerce.mapper.ProductMapper;
import com.smartecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartService {

    private final ProductMapper productMapper;

    @Override
    public List<CartItem> getCart(Long userId) {
        return list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .orderByDesc(CartItem::getUpdatedAt));
    }

    @Override
    @Transactional
    public void addToCart(Long userId, Long productId, int quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new RuntimeException("商品不存在或已下架");
        }
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        CartItem existing = getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId));
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            updateById(existing);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setQuantity(quantity);
            save(item);
        }
    }

    @Override
    @Transactional
    public void updateQuantity(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(userId, productId);
            return;
        }
        CartItem item = getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId));
        if (item != null) {
            item.setQuantity(quantity);
            updateById(item);
        }
    }

    @Override
    public void removeFromCart(Long userId, Long productId) {
        remove(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId));
    }

    @Override
    public void clearCart(Long userId) {
        remove(new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId));
    }
}
