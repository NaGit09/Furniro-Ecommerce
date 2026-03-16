package com.example.backend.dto.Response.Product;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ProductCompareRes {
    private Integer productId;
    private String name;
    private Integer price;
    private String image;

    private Integer width;
    private Integer height;
    private Integer depth;
    private Integer weight;

    private String material;
    private String warranty;


}
