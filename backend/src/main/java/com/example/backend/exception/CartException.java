package com.example.backend.exception;

import com.example.backend.common.enums.Order.CartErrorCode;
import lombok.Getter;

@Getter
public class CartException extends RuntimeException {

    private final CartErrorCode orderErrorCode;

    public CartException(CartErrorCode orderErrorCode) {
        this.orderErrorCode = orderErrorCode;
    }
}
