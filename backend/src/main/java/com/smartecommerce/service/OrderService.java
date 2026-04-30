package com.smartecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartecommerce.entity.Order;
import com.smartecommerce.entity.OrderItem;
import com.smartecommerce.dto.*;

import java.util.List;

public interface OrderService extends IService<Order> {
    OrderVO createOrder(Long userId, CreateOrderDTO dto);
    List<OrderVO> getOrderList(Long userId);
    OrderVO getOrderDetail(Long userId, Long orderId);
    void payOrder(Long userId, Long orderId);
    void cancelOrder(Long userId, Long orderId);
}
