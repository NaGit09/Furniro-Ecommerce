package com.example.backend.database.repository.Order;

import com.example.backend.database.entity.Order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser_UserIDOrderByOrderedAtDesc(Integer userId);

    // Thống kê doanh thu theo trạng thái thanh toán
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = 'PAID'")
    Long calculateTotalRevenue();
}
