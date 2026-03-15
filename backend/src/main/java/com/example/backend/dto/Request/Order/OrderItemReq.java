package com.example.backend.dto.Request.Order;

import lombok.Data;

@Data
public class OrderItemReq {
    private Integer productVariantID;
    private Integer quantity;
    private Integer price;
}
