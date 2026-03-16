package com.example.backend.controller;

import com.example.backend.database.entity.Product.Product;
import com.example.backend.dto.Response.Product.ProductCompareRes;
import com.example.backend.dto.Response.Product.ProductDetailRes;
import com.example.backend.dto.Response.Product.ProductListRes;
import com.example.backend.service.Product.ProductService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor

public class ProductController {
    private final ProductService productService;
    @GetMapping("/products")
    public Page<ProductListRes> getProducts(@RequestParam int page,
                                            @RequestParam int size){
        return productService.getProducts(page,size);
    }
    @GetMapping("/product/{id}")
    public ProductDetailRes getProductDetail( @PathVariable Integer id){
        return productService.getProductDetail(id);
    }
    @GetMapping("/products/compare")
    public List<ProductCompareRes> compareProducts(@RequestParam @Size(min = 2,max = 3) List<Integer> ids) {
            return  productService.compareProducts(ids);
    }
    }

