package com.example.backend.dto.Request.User;

import lombok.Data;

@Data
public class ConfirmOTPReq {
    private String email;
    private String otp;
}
