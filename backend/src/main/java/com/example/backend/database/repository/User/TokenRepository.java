package com.example.backend.database.repository.User;

import com.example.backend.database.entity.User.Account;
import com.example.backend.database.entity.User.ExistingTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository  extends JpaRepository<ExistingTokens, Long> {

    // Find token with token receive
    Optional<ExistingTokens> findByToken(String token);

    // Find token with Account
    Optional<ExistingTokens> findByAccount(Account account);
}
