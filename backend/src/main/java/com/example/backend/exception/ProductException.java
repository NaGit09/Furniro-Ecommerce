package com.example.backend.exception;

import com.example.backend.common.enums.Product.ProductErrorCode;
import lombok.Getter;

@Getter
public class ProductException extends RuntimeException {
    private final ProductErrorCode productErrorCode;
    public ProductException(ProductErrorCode errorCode) {
        this.productErrorCode = errorCode;
    }
}
