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

        // lấy thông tin user từ database với gmail
        Account user = accountRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // trả về user context cho security xác thực
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getPasswordHash())
                .authorities(user.getRole().name())
                .build();
    }
}
