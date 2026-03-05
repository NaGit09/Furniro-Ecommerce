package com.example.backend.database.entity.Warehouse;
import com.example.backend.common.enums.Warehouse.ReservationStatus;
import com.example.backend.database.entity.Order.Order;
import com.example.backend.database.entity.Product.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity @Table(name = "StockReservation")
@Getter @Setter
public class StockReservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reserveID;

    @ManyToOne @JoinColumn(name = "VariantID", nullable = false)
    private ProductVariant variant;

    @ManyToOne @JoinColumn(name = "OrderID", nullable = false)
    private Order order;

    private Integer quantity;
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.ACTIVE;
}