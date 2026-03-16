package com.example.backend.service.Product;

import com.example.backend.database.entity.Product.Product;
import com.example.backend.database.entity.Product.ProductImage;
import com.example.backend.database.entity.Product.ProductVariant;
import com.example.backend.database.repository.Product.ProductRepository;
import com.example.backend.dto.Response.Product.ProductCompareRes;
import com.example.backend.dto.Response.Product.ProductDetailRes;
import com.example.backend.dto.Response.Product.ProductListRes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductListRes> getProducts(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.getProductList(pageable);
    }
    public ProductDetailRes getProductDetail(Integer productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));


        return ProductDetailRes.builder()
                //Product
                .productId(product.getProductID())
                .name(product.getName())
                .basePrice(product.getBasePrice())
                .description(product.getDescription())
                .brand(product.getBrand())
                .status(product.getStatus().toString())
                // Category
                .categoryName(product.getCategory().getCategoryName())

                // Images
                .images(
                        product.getImages()
                                .stream()
                                .map(ProductImage::getUrl)
                                .toList()
                )
                // Variant
                .sizes(product.getVariants().stream().map(v -> v.getSize().getSizeName()).distinct().toList())
                .colors(product.getVariants().stream().map(v-> v.getColor().getColorName()).distinct().toList())
                .skus(product.getVariants().stream().map(ProductVariant::getSku).distinct().toList())

                // Specification
                .width(product.getSpecification().getWidth())
                .height(product.getSpecification().getHeight())
                .depth(product.getSpecification().getDepth())
                .weight(product.getSpecification().getWeight())
                .material(product.getSpecification().getMaterial())
                .configuration(product.getSpecification().getConfiguration())

                // Warranty
                .warrantyType(product.getWarranty().getType())
                .warrantyDuration(product.getWarranty().getDuration())
                .warrantySummary(product.getWarranty().getSummary())
                .build();
    }

    // === PRODUCT COMPARISON ===
    public List<ProductCompareRes> compareProducts(List<Integer> ids) {
        if(ids.size() != new HashSet<>(ids).size()){
            throw new RuntimeException("Duplicate product ids");
        }
        return this.productRepository.compareProducts(ids);
    }

}
