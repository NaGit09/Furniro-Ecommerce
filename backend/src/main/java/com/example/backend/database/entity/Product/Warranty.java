package com.example.backend.database.entity.Product;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "Warranty")
@Getter @Setter
public class Warranty {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer warrantyID;
    private String type, duration;
    @Column(columnDefinition = "TEXT")
    private String summary;

    @OneToOne @JoinColumn(name = "ProductID")
    private Product product;
}
