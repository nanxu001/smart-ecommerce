package com.smartecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartecommerce.entity.Product;
import com.smartecommerce.entity.UserBehavior;

import java.util.List;

public interface BehaviorService extends IService<UserBehavior> {
    void recordBehavior(Long userId, Long productId, String type, String searchQuery);
    List<Product> getHistory(Long userId, int limit);
}
