package com.example.backend.database.entity.Product;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "ColorMaster")
@Getter @Setter
public class ColorMaster {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer colorID;
    private String colorName;
}