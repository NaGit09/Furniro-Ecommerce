package com.example.backend.database.entity.Order;
import com.example.backend.common.enums.Order.OrderStatus;
import com.example.backend.common.enums.Order.PaymentMethod;
import com.example.backend.common.enums.Order.PaymentStatus;
import com.example.backend.database.entity.User.Address;
import com.example.backend.database.entity.User.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name = "OrderTable")
@Getter @Setter
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderID;

    @ManyToOne @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @ManyToOne @JoinColumn(name = "AddressID", nullable = false)
    private Address address;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    private String transactionID;
    private Integer totalAmount;
    private Integer shippingFee = 0;

    @Column(columnDefinition = "TEXT")
    private String orderNote;

    @CreationTimestamp
    private LocalDateTime orderedAt;

    private LocalDateTime paidAt;
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}