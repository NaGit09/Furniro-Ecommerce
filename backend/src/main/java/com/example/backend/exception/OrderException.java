package com.example.backend.exception;

import com.example.backend.common.enums.Order.OrderErrorCode;
import lombok.Getter;

@Getter
public class OrderException extends RuntimeException {
    private final OrderErrorCode orderErrorCode;
    public OrderException(OrderErrorCode errorCode) {
        this.orderErrorCode = errorCode;
    }
}
