package com.example.backend.database.entity.Product;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "ProductVariant")
@Getter @Setter
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer variantID;

    @Column(unique = true)
    private String sku;

    private Integer price;
    private Integer stockQuantity = 0;

    @ManyToOne @JoinColumn(name = "ProductID")
    private Product product;

    @ManyToOne @JoinColumn(name = "ColorID")
    private ColorMaster color;

    @ManyToOne @JoinColumn(name = "SizeID")
    private SizeMaster size;
}
