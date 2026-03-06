package com.example.backend.database.repository.User;

import com.example.backend.database.entity.User.Address;
import com.example.backend.database.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    Optional<Address> findByUser(User user);
}