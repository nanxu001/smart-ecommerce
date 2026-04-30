package com.smartecommerce.dto;

import lombok.Data;

@Data
public class OrderItemVO {
    private Long productId;
    private String productName;
    private java.math.BigDecimal price;
    private Integer quantity;
}
