package com.example.backend.dto.Request.User;

import lombok.Data;

@Data
public class ConfirmOTP {
    private String email;
    private String otp;
}
