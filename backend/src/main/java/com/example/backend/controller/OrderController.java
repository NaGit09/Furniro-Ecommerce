package com.example.backend.controller;

import com.example.backend.dto.API.AType;
import com.example.backend.dto.Request.Order.CreateOrderReq;
import com.example.backend.service.Order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name = "/Order")
@RequiredArgsConstructor

public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<AType> createOrder(@RequestBody CreateOrderReq order) {
        return orderService.createOrder(order);
    }
}
