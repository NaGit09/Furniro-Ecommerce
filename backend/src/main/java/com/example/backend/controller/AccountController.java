package com.example.backend.controller;

import com.example.backend.dto.API.AType;
import com.example.backend.dto.Request.User.ChangePasswordReq;
import com.example.backend.dto.Request.User.LoginReq;
import com.example.backend.dto.Request.User.RegisterReq;
import com.example.backend.service.User.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<AType> register(@RequestBody RegisterReq registerReq) {
        return accountService.registerAccount(registerReq);
    }

    @GetMapping("/confirm")
    public ResponseEntity<AType> confirm(@RequestParam String token) {
        return accountService.confirmActive(token);
    }

    @PostMapping("/login")
    public ResponseEntity<AType> login(@RequestBody LoginReq loginReq) {
        return accountService.loginAccount(loginReq);
    }

    @PostMapping("/logout")
    public ResponseEntity<AType> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        return accountService.logoutAccount(token);
    }

    @PostMapping("/sendOTP")
    public ResponseEntity<AType> sendOPT(@RequestBody String email) {
        return accountService.sendOTP(email);
    }

    @PostMapping("/confirmOTP")
    public ResponseEntity<AType> confirmOTP(@RequestBody String otp) {
        return accountService.confirmOTP(otp);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<AType> changePassword(@RequestBody ChangePasswordReq changePasswordReq) {
        return accountService.changePassword(changePasswordReq);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AType> refreshToken(@RequestHeader("Authorization") String token) {
        String refreshToken = token.substring(7);
        System.out.println(refreshToken);
        return accountService.refreshToken(refreshToken);
    }

    // ADMIN API
    @PostMapping("/resetPassword")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AType> resetPassword(@RequestBody List<Integer> accountIDs) {
        return accountService.resetPassword(accountIDs);
    }

    @PostMapping("/ban")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AType> ban(@RequestBody List<Integer> accountIDs) {
        return accountService.banAccount(accountIDs);
    }

    @PostMapping("/unban")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AType> unban(@RequestBody List<Integer> accountIDs) {
        return accountService.unbanAccount(accountIDs);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AType> delete(@RequestBody List<Integer> accountIDs) {
        return accountService.deleteAccount(accountIDs);
    }
}
