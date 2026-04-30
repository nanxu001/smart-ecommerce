package com.smartecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private BigDecimal totalAmount;
    private String status;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemVO> items;
}
