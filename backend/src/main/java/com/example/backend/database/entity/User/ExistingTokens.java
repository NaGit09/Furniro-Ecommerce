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
public class ExistingTokens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 500)
    private String token;

    @Column(name = "TokenType")
    private String tokenType; // "REFRESH"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AccountID")
    private Account account;
}