package com.example.backend.database.entity.Warehouse;
import com.example.backend.database.entity.Product.ProductVariant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity @Table(name = "Inventory")
@Getter @Setter
public class Inventory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer inventoryID;

    @ManyToOne @JoinColumn(name = "VariantID", nullable = false)
    private ProductVariant variant;

    @ManyToOne @JoinColumn(name = "WarehouseID", nullable = false)
    private Warehouse warehouse;

    private Integer quantity = 0;
    private Integer reservedQuantity = 0;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}