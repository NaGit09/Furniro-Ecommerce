package com.example.backend.database.repository.Order;

import com.example.backend.database.entity.Order.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository  extends JpaRepository<Payment , Integer> {

}
