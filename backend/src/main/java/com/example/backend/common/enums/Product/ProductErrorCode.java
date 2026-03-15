package com.example.backend.common.enums.Product;

import lombok.Data;
import lombok.Getter;

@Getter
public enum ProductErrorCode {
    PRODUCT_NOT_FOUND (404 , "Product Not Found"),;

    private final int code;
    private final String message;

    ProductErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
