package com.example.backend.database.repository.Product;

import com.example.backend.database.entity.Product.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    List<ProductVariant> findByProduct_ProductID(Integer productId);

    // Tìm biến thể cụ thể theo Product, Color và Size
    Optional<ProductVariant> findByProduct_ProductIDAndColor_ColorIDAndSize_SizeID(
            Integer productId, Integer colorId, Integer sizeId);
}
