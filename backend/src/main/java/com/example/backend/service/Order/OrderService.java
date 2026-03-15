package com.example.backend.service.Order;

import com.example.backend.common.enums.Order.OrderErrorCode;
import com.example.backend.common.enums.Order.OrderStatus;
import com.example.backend.database.entity.Order.Cart;
import com.example.backend.database.entity.Order.Order;
import com.example.backend.database.entity.Order.OrderItem;
import com.example.backend.database.entity.Order.Payment;
import com.example.backend.database.entity.Product.ProductVariant;
import com.example.backend.database.repository.Order.OrderItemRepository;
import com.example.backend.database.repository.Order.OrderRepository;
import com.example.backend.database.repository.Order.PaymentRepository;
import com.example.backend.dto.API.AType;
import com.example.backend.dto.API.ApiType;
import com.example.backend.dto.Request.Order.CreateOrderReq;
import com.example.backend.dto.Request.Order.UpdateStatusOrder;
import com.example.backend.exception.OrderException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;


    //     create order
    @Transactional
    public ResponseEntity<AType> createOrder(CreateOrderReq orderReq) {

        // 1. Create order
        Order order = Order.builder()
                .orderNote(orderReq.getOrderNote())
                .address(orderReq.getAddress())
                .shippingFee(orderReq.getShippingFee())
                .build();

        Order orderResult = orderRepository.save(order);

        // 2. Create order item
        List<OrderItem> orderItems = orderReq.getOrderItems().stream().map(
                item -> OrderItem.builder()
                        .variant(item.getProductVariantID())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPrice())
                        .order(orderResult)
                        .build()
        ).toList();

        orderItemRepository.saveAll(orderItems);

        // 3. create payment
        Payment payment = Payment.builder()
                .amount(orderResult.getTotalAmount())
                .paymentMethod(orderReq.getPaymentMethod())
                .paymentStatus(orderReq.getPaymentStatus())
                .currency(orderReq.getCurrency())
                .order(orderResult)
                .build();

        paymentRepository.save(payment);

        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Order created successfully")
                .data(true)
                .build());
    }

    // change order status
    public ResponseEntity<AType> changeStatusOrder(UpdateStatusOrder updateStatusOrder) {

        Order order = orderRepository.findById(updateStatusOrder.getOrderId()).orElseThrow(
                () -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST)
        );

        order.setStatus(updateStatusOrder.getOrderStatus());

        orderRepository.save(order);


        return ResponseEntity.ok(ApiType.builder()
                        .code(200)
                        .message("Order confirmed successfully")
                .data(true)
                .build());
    }



}
