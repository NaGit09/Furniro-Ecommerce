package com.example.backend.dto.Response.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ProductDetailRes {
    // Product
    private Integer productId;
    private String name;
    private String description;
    private Integer basePrice;
    private String brand;
    private String status;

    // Category
    private String categoryName;

    // Images
    private List<String> images;

    // Variant
    private List<String> sizes;
    private List<String> colors;
    private List<String> skus;

    // Specification
    private Integer width;
    private Integer height;
    private Integer depth;
    private Integer weight;
    private String material;
    private String configuration;

    // Warranty
    private String warrantyType;
    private String warrantyDuration;
    private String warrantySummary;
}
