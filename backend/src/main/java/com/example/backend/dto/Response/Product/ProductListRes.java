package com.example.backend.dto.Response.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class ProductListRes {
    private Integer productId;
    private String name;
    private Integer basePrice;
    private String url;

}
