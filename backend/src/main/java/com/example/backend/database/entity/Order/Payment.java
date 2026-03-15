package com.example.backend.database.entity.Order;

import com.example.backend.common.enums.Order.PaymentMethod;
import com.example.backend.common.enums.Order.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentID;

    @ManyToOne
    @JoinColumn(name = "OrderID")
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private String transactionID;

    private Integer amount;

    private String currency = "VND";

    @Column(columnDefinition = "TEXT")
    private String providerResponse = "";

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime paidAt;

}