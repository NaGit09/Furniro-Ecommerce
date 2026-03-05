package com.example.backend.database.repository.Warehouse;

import com.example.backend.database.entity.Warehouse.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    // Sử dụng PESSIMISTIC_WRITE để khóa dòng này lại khi đang cập nhật số lượng
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.variant.variantID = :variantId AND i.warehouse.warehouseID = :warehouseId")
    Optional<Inventory> findByVariantAndWarehouseForUpdate(Integer variantId, Integer warehouseId);

    List<Inventory> findByVariant_VariantID(Integer variantId);
}
