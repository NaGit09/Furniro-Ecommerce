package com.example.backend.dto.Request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangePasswordReq {
    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String password;

    @NotEmpty
    private String confirmPassword;
}
