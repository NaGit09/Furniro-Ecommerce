package com.example.backend.controller;

import com.example.backend.dto.API.AType;
import com.example.backend.dto.Request.User.ChangePasswordReq;
import com.example.backend.dto.Request.User.LoginReq;
import com.example.backend.dto.Request.User.RegisterReq;
import com.example.backend.service.User.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public AType registerAccount(@RequestBody RegisterReq registerReq) {
        return accountService.registerAccount(registerReq);
    }

    @GetMapping("/confirm")
    public AType confirmAccount(@RequestParam String token) {
        return accountService.confirmActive(token);
    }

    @PostMapping("/login")
    public AType loginAccount (@RequestBody LoginReq loginReq) {
        return accountService.loginAccount(loginReq);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER')")
    public AType logoutAccount(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        return accountService.logoutAccount(token);
    }
    @PostMapping("/sendOTP")
    public AType sendOPT(@RequestBody String email) {
        return accountService.sendOTP(email);
    }

    @PostMapping("/confirmOTP")
    public AType confirmOTP(@RequestBody String otp) {
        return accountService.confirmOTP(otp);
    }

    @PostMapping("/changePassword")
    public AType changePassword(@RequestBody ChangePasswordReq changePasswordReq) {
        return accountService.changePassword(changePasswordReq);
    }

    @PostMapping("/resetPassword")
    @PreAuthorize("hasRole('ADMIN')")
    public AType resetPassword(@RequestBody int accountID) {
        return accountService.resetPassword(accountID);
    }

    @PostMapping("/banAccount")
    @PreAuthorize("hasRole('ADMIN')")
    public AType banAccount(@RequestBody int accountId) {
        return accountService.banAccount(accountId);
    }
}
