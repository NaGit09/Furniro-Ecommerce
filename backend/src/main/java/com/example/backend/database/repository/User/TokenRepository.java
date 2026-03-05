package com.example.backend.database.repository.User;

import com.example.backend.database.entity.User.Account;
import com.example.backend.database.entity.User.ExistingTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository  extends JpaRepository<ExistingTokens, Long> {
    // Tìm tất cả token hợp lệ (chưa bị hủy, chưa hết hạn) của một Account
    @Query("""
        select t from ExistingTokens t inner join Account a\s
        on t.account.accountID = a.accountID\s
        where a.accountID = :accountId and (t.expired = false or t.revoked = false)
   \s""")
    List<ExistingTokens> findAllValidTokensByAccount(Long accountId);

    // Tìm chính xác một chuỗi token
    Optional<ExistingTokens> findByToken(String token);

    Optional<ExistingTokens> findByAccount(Account account);
}
