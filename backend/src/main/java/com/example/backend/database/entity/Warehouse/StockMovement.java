package com.example.backend.database.entity.Warehouse;
import com.example.backend.common.enums.Warehouse.MovementType;
import com.example.backend.database.entity.Product.ProductVariant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity @Table(name = "StockMovement")
@Getter @Setter
public class StockMovement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movementID;

    @ManyToOne @JoinColumn(name = "VariantID", nullable = false)
    private ProductVariant variant;

    @ManyToOne @JoinColumn(name = "WarehouseID", nullable = false)
    private Warehouse warehouse;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private MovementType type;

    private Integer referenceID; // OrderID hoặc ImportNoteID
    private String note;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private Integer createdBy; // AccountID
}