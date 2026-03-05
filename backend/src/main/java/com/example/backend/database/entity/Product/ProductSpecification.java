package com.example.backend.database.entity.Product;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "ProductSpecification")
@Getter @Setter
public class ProductSpecification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer specID;
    private Integer width, height, depth, weight;
    private String material, configuration;

    @OneToOne @JoinColumn(name = "ProductID")
    private Product product;
}
