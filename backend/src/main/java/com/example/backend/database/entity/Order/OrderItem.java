package com.example.backend.database.entity.Order;
import com.example.backend.database.entity.Product.ProductVariant;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "OrderItem")
@Getter @Setter
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemID;

    @ManyToOne @JoinColumn(name = "OrderID")
    private Order order;

    @ManyToOne @JoinColumn(name = "VariantID")
    private ProductVariant variant;

    private Integer quantity;
    private Integer priceAtPurchase; // Lưu giá lúc mua để làm hóa đơn
}