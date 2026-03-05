package com.example.backend.database.repository.User;
import com.example.backend.database.entity.User.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);
    // Tìm kiếm user kèm theo danh sách địa chỉ (dùng EntityGraph để tránh N+1)
    @EntityGraph(attributePaths = {"addresses"})
    Optional<User> findWithAddressesByUserID(Integer userID);
}
