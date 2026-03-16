package com.example.backend.database.entity.Order;
import com.example.backend.database.entity.Product.ProductVariant;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "OrderItem")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemID;

    @ManyToOne @JoinColumn(name = "OrderID")
    private Order order;

    @Column(name = "VariantID")
    private Integer variant;

    private Integer quantity;

    // Price after sale
    private Integer priceAtPurchase;
}