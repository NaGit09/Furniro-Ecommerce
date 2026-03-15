package com.example.backend.dto.Request.User;

import com.example.backend.common.enums.User.AddressType;
import com.example.backend.common.enums.User.Gender;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data

public class UpdateProfileReq {

    private String username;
    private String email;
    private String password;
    private String phone;

    private String firstName;
    private String lastName;
    private Gender gender;
    private MultipartFile avatar;
    private LocalDate birthday;

    private String receiveName;
    private String receivePhone;
    private String province;
    private String district;
    private String ward;
    private String street;
    private AddressType type;
}
