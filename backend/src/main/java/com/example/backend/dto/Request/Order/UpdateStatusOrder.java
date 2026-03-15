package com.example.backend.dto.Request.Order;

import com.example.backend.common.enums.Order.OrderStatus;
import lombok.Data;

@Data
public class UpdateStatusOrder {
    private Integer orderId;
    private OrderStatus orderStatus;
}
