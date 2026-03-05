package com.example.backend.dto.Response.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRes {
    private String AccessToken;
    private String RefreshToken;
    private String FirstName;
    private String LastName;
    private String UserName;
    private String AvatarUrl;
    private String Email;
}
