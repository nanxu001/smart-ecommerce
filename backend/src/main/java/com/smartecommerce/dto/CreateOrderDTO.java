package com.smartecommerce.dto;

import lombok.Data;

@Data
public class CreateOrderDTO {
    private Long[] cartIds;
    private String address;
}
