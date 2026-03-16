package com.example.backend.dto.Request.Order;

import lombok.Data;

@Data
public class RemoveCartItemReq {

    private int userId;
    private int variantId;

}
