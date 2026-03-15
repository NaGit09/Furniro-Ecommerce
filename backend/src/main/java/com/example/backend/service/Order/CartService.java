package com.example.backend.service.Order;


import com.example.backend.common.enums.Order.CartErrorCode;
import com.example.backend.common.enums.Product.ProductErrorCode;
import com.example.backend.common.utils.CartUtil;
import com.example.backend.database.entity.Order.Cart;
import com.example.backend.database.entity.Order.CartItem;
import com.example.backend.database.entity.Product.ProductVariant;
import com.example.backend.database.repository.Order.CartItemRepository;
import com.example.backend.database.repository.Order.CartRepository;
import com.example.backend.database.repository.Product.ProductVariantRepository;
import com.example.backend.dto.API.AType;
import com.example.backend.dto.API.ApiType;
import com.example.backend.dto.Request.Order.AddToCartReq;
import com.example.backend.dto.Request.Order.RemoveCartItemReq;
import com.example.backend.dto.Request.Order.UpdateCartReq;
import com.example.backend.exception.CartException;
import com.example.backend.exception.ProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional
    public ResponseEntity<AType> addToCart(AddToCartReq req) {

        // 1. Find cart
        Cart cart = cartRepository.findByUser_UserID(req.getUserId())
                .orElseThrow(() -> new CartException(CartErrorCode.CART_NOT_EXIST));

        // 2. Find product variant
        ProductVariant variant = productVariantRepository.findById(req.getVariantId())
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // 3. Check cart item exist
        Optional<CartItem> cartItemOpt =
                cartItemRepository.findByCartAndVariant(cart, variant);

        CartItem cartItem;

        if (cartItemOpt.isPresent()) {

            // update quantity
            cartItem = cartItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + req.getQuantity());

        } else {

            // create new cart item
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setVariant(variant);
            cartItem.setQuantity(req.getQuantity());

        }

        cartItemRepository.save(cartItem);

        return ResponseEntity.ok(
                ApiType.builder()
                        .code(200)
                        .message("Add to cart successfully")
                        .data(true)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<AType> removeCartItem(RemoveCartItemReq req) {

        // 1. Find cart
        Cart cart = cartRepository.findByUser_UserID(req.getUserId())
                .orElseThrow(() -> new CartException(CartErrorCode.CART_NOT_EXIST));

        // 2. Find cart item
        CartItem cartItem = cartItemRepository
                .findByCartAndVariant_VariantID(cart, req.getVariantId())
                .orElseThrow(() -> new CartException(CartErrorCode.CART_ITEM_NOT_EXIST));

        // 3. Delete cart item
        cartItemRepository.delete(cartItem);

        // 4. Return response
        return ResponseEntity.ok(
                ApiType.builder()
                        .code(200)
                        .message("Remove cart item successfully")
                        .data(true)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<AType> updateCart(UpdateCartReq req) {

        Cart cart = cartRepository.findByUser_UserID(req.getUserId())
                .orElseThrow(() -> new CartException(CartErrorCode.CART_NOT_EXIST));

        CartItem cartItem = cartItemRepository
                .findByCartAndVariant_VariantID(cart, req.getVariantId())
                .orElseThrow(() -> new CartException(CartErrorCode.CART_ITEM_NOT_EXIST));

        int newQuantity = CartUtil.calculateQuantity(
                cartItem.getQuantity(),
                req.getQuantity(),
                req.getAction()
        );

        if(newQuantity <= 0){
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        }

        return ResponseEntity.ok(
                ApiType.builder()
                        .code(200)
                        .message("Update cart successfully")
                        .data(true)
                        .build()
        );
    }

}
