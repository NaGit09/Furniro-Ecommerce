package com.example.backend.database.repository.Mail;

import com.example.backend.database.entity.Email.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationToken , Integer> {
    Optional<VerificationToken> findByToken(String token);

    void removeByToken(String token);
}
