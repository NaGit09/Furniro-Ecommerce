package com.example.backend.database.entity.User;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ExistingTokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExistingToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 500)
    private String token;

    @Column(name = "TokenType")
    private String tokenType; // "REFRESH"

    private boolean revoked;

    private boolean expired;

    @Column(name = "ExpireDate", nullable = false)
    private LocalDateTime expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AccountID")
    private Account account;
}