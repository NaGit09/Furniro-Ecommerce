package com.example.backend.database.entity.Product;
import com.example.backend.common.enums.Product.LicenseType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity @Table(name = "ProductLicense")
@Getter @Setter
public class ProductLicense {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer licenseID;
    private String licenseName;
    @Enumerated(EnumType.STRING)
    private LicenseType licenseType;
    private String documentUrl;
    private LocalDate issueDate;
    private LocalDate expiryDate;

    @ManyToOne @JoinColumn(name = "ProductID")
    private Product product;
}
