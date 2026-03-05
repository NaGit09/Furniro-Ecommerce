package com.example.backend.database.repository.Warehouse;

import com.example.backend.common.enums.Warehouse.ReservationStatus;
import com.example.backend.database.entity.Warehouse.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, Integer> {
    // Tìm các bản ghi giữ hàng đã hết hạn để giải phóng kho
    List<StockReservation> findByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime now);
}