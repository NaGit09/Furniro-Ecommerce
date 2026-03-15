package com.example.backend.dto.Request.Order;

import com.example.backend.common.enums.Order.OrderStatus;
import com.example.backend.common.enums.Order.PaymentMethod;
import com.example.backend.common.enums.Order.PaymentStatus;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderReq {

    private double totalAmount;
    private String orderNote;
    private int shippingFee;
    private OrderStatus orderStatus;
    private String address;

    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    private String currency ;

    private List<OrderItemReq> orderItems;

}
