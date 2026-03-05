package com.example.backend.database.entity.Product;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "SizeMaster")
@Getter @Setter
public class SizeMaster {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sizeID;
    private String sizeName;
}
