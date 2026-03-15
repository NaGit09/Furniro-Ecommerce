package com.example.backend.database.entity.Order;
import com.example.backend.common.enums.Order.OrderStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "OrderTable")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderID;

    @Column(name = "UserID")
    private Integer userID;

    private  String address;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private Integer totalAmount;

    private Integer shippingFee = 0;

    @Column(columnDefinition = "TEXT")
    private String orderNote;

    private LocalDateTime orderedAt = LocalDateTime.now();

    private LocalDateTime completedAt;

    // Relationship
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payment> payments;

}