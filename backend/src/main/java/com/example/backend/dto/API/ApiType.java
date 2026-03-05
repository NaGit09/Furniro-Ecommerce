package com.example.backend.common.API;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiType <T> {
    public int code;
    public String message;
    public T data;
}
