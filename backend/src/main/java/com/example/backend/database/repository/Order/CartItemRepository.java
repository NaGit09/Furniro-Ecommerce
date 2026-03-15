package com.example.backend.database.repository.Order;

import com.example.backend.database.entity.Order.Cart;
import com.example.backend.database.entity.Order.CartItem;
import com.example.backend.database.entity.Product.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findCartItemByCart(Cart cart);

    Optional<CartItem> findByCartAndVariant_VariantID(Cart cart, int variantId);

    Optional<CartItem> findByCartAndVariant(Cart cart, ProductVariant variant);
}
