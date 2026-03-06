package com.example.backend.controller;

import com.example.backend.dto.API.AType;
import com.example.backend.dto.Request.User.UpdateProfile;
import com.example.backend.service.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name = "/User")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AType updateProfile(@AuthenticationPrincipal UserDetails currentUser, @ModelAttribute UpdateProfile request) {
        return userService.updateProfile(currentUser.getUsername(),request);
    }
}
