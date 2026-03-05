package com.example.backend.database.repository.Product;

import com.example.backend.common.enums.Product.ProductStatus;
import com.example.backend.database.entity.Product.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page; // Đúng thư viện của Spring
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    // Tìm sản phẩm theo danh mục và trạng thái ACTIVE
    Page<Product> findByCategory_CategoryIDAndStatus(Integer categoryId, ProductStatus status, Pageable pageable);

    // Tìm kiếm sản phẩm theo tên (có phân trang)
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
