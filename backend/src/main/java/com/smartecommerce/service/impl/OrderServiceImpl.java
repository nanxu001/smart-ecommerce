package com.smartecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartecommerce.dto.CreateOrderDTO;
import com.smartecommerce.dto.OrderItemVO;
import com.smartecommerce.dto.OrderVO;
import com.smartecommerce.entity.CartItem;
import com.smartecommerce.entity.Order;
import com.smartecommerce.entity.OrderItem;
import com.smartecommerce.entity.Product;
import com.smartecommerce.mapper.CartItemMapper;
import com.smartecommerce.mapper.OrderItemMapper;
import com.smartecommerce.mapper.OrderMapper;
import com.smartecommerce.mapper.ProductMapper;
import com.smartecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final CartItemMapper cartItemMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public OrderVO createOrder(Long userId, CreateOrderDTO dto) {
        List<CartItem> cartItems = cartItemMapper.selectList(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .in(CartItem::getId, dto.getCartIds()));
        if (cartItems.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = productMapper.selectById(cartItem.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new RuntimeException("商品 " + cartItem.getProductId() + " 不可购买");
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("商品 " + product.getName() + " 库存不足");
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);

            // 扣减库存
            product.setStock(product.getStock() - cartItem.getQuantity());
            product.setSales(product.getSales() + cartItem.getQuantity());
            productMapper.updateById(product);
        }

        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setTotalAmount(totalAmount);
        order.setStatus("pending");
        order.setAddress(dto.getAddress());
        save(order);

        // 保存订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        // 清空已购买的购物车项
        cartItemMapper.delete(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .in(CartItem::getId, dto.getCartIds()));

        return buildOrderVO(order, orderItems);
    }

    @Override
    public List<OrderVO> getOrderList(Long userId) {
        List<Order> orders = list(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreatedAt));
        List<OrderVO> result = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
            result.add(buildOrderVO(order, items));
        }
        return result;
    }

    @Override
    public OrderVO getOrderDetail(Long userId, Long orderId) {
        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        return buildOrderVO(order, items);
    }

    @Override
    @Transactional
    public void payOrder(Long userId, Long orderId) {
        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("订单状态异常");
        }
        order.setStatus("paid");
        updateById(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("只能取消待支付订单");
        }
        order.setStatus("cancelled");
        updateById(order);

        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(Math.max(0, product.getSales() - item.getQuantity()));
                productMapper.updateById(product);
            }
        }
    }

    private String generateOrderNo() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
    }

    private OrderVO buildOrderVO(Order order, List<OrderItem> items) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        vo.setAddress(order.getAddress());
        vo.setCreatedAt(order.getCreatedAt());
        vo.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemVO> itemVOs = new ArrayList<>();
        for (OrderItem item : items) {
            OrderItemVO itemVO = new OrderItemVO();
            itemVO.setProductId(item.getProductId());
            itemVO.setProductName(item.getProductName());
            itemVO.setPrice(item.getPrice());
            itemVO.setQuantity(item.getQuantity());
            itemVOs.add(itemVO);
        }
        vo.setItems(itemVOs);
        return vo;
    }
}
