package com.example.backend.dto.Request.Order;


import com.example.backend.common.enums.Order.CartAction;
import lombok.Data;

@Data
public class UpdateCartReq {
    private int userId;
    private int variantId;
    private int quantity;
    private CartAction action;
}