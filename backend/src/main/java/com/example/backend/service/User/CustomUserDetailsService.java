package com.example.backend.service.User;

import com.example.backend.database.entity.User.Account;
import com.example.backend.database.repository.User.AccountRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService  implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        // Load user info form database
        Account user = accountRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // Return user context for spring security
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getPasswordHash())
                .authorities(user.getRole().name())
                .build();
    }
}
