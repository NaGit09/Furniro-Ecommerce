package com.example.backend.service.Product;

import com.example.backend.database.entity.Product.Product;
import com.example.backend.database.repository.Product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;



    public Product getProductByID(int id){
        return productRepository
                .findById(id).orElseThrow(RuntimeException::new);
    }
}
