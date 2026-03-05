package com.example.backend.common.API;

import lombok.Data;

@Data
public class ErrorType <T> {
    private String code;
    private String message;
    private T data;
}
