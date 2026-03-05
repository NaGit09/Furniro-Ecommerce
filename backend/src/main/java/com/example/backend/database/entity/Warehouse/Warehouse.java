package com.example.backend.database.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity @Table(name = "Warehouse")
@Getter @Setter
public class Warehouse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer warehouseID;

    @Column(nullable = false, length = 150)
    private String name;

    private String address;
    private Boolean isActive = true;

    @OneToMany(mappedBy = "warehouse")
    private List<Inventory> inventories;
}