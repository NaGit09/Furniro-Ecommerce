package com.example.backend.database.repository.Product;

import com.example.backend.common.enums.Product.ProductStatus;
import com.example.backend.database.entity.Product.Product;
import com.example.backend.dto.Response.Product.ProductCompareRes;
import com.example.backend.dto.Response.Product.ProductListRes;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    // Tìm sản phẩm theo danh mục và trạng thái ACTIVE
    Page<Product> findByCategory_CategoryIDAndStatus(Integer categoryId, ProductStatus status, Pageable pageable);

    // Tìm kiếm sản phẩm theo tên (có phân trang)
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("""
        SELECT new com.example.backend.dto.Response.Product.ProductListRes(
            p.productID,
            p.name,
            p.basePrice,
            pi.url
        )
        FROM Product p
        JOIN p.images pi
        WHERE pi.sortOrder = 1
    """)
    Page<ProductListRes> getProductList(Pageable pageable);
    @NullMarked
    Optional<Product> findById(Integer id);
    @Query("""
    SELECT new com.example.backend.dto.Response.Product.ProductCompareRes(
        p.productID,
        p.name,
        p.basePrice,
        pi.url,
        s.width,
        s.height,
        s.depth,
        s.weight,
        s.material,
        w.type
    )
    FROM Product p
    LEFT JOIN p.images pi
    LEFT JOIN p.specification s
    LEFT JOIN p.warranty w
    WHERE p.productID IN :ids
    AND pi.sortOrder = 1
""")
    List<ProductCompareRes> compareProducts(List<Integer> ids);
}
