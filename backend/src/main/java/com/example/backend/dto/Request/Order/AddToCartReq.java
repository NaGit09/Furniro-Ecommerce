package com.example.backend.dto.Request.Order;

import lombok.Data;

@Data
public class AddToCartReq {
    private int userId;
    private int variantId;
    private int quantity;
}
