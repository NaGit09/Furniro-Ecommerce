package com.example.backend.database.entity.User;

import com.example.backend.common.enums.User.AddressType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressID;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String district;

    private String ward;

    private String street;

    private Boolean isDefault = false;

    @Enumerated(EnumType.STRING)
    private AddressType addressType = AddressType.HOME;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;
}