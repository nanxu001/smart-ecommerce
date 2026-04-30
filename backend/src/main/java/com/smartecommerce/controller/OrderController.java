package com.smartecommerce.controller;

import com.smartecommerce.common.Result;
import com.smartecommerce.dto.CreateOrderDTO;
import com.smartecommerce.dto.OrderVO;
import com.smartecommerce.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<OrderVO> create(@RequestBody CreateOrderDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.createOrder(userId, dto));
    }

    @GetMapping
    public Result<List<OrderVO>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.getOrderList(userId));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.getOrderDetail(userId, id));
    }

    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.payOrder(userId, id);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancelOrder(userId, id);
        return Result.success();
    }
}
