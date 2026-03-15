package com.example.backend.controller;

import com.example.backend.database.entity.Order.Cart;
import com.example.backend.dto.API.AType;
import com.example.backend.dto.Request.Order.AddToCartReq;
import com.example.backend.service.Order.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name = "/Cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<AType> addCart(@RequestBody AddToCartReq cart) {
        return cartService.addToCart(cart);
    }
}
