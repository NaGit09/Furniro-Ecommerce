package com.example.backend.service.User;

import com.example.backend.database.entity.User.Account;
import com.example.backend.database.entity.User.Address;
import com.example.backend.database.entity.User.User;
import com.example.backend.database.repository.User.AccountRepository;
import com.example.backend.database.repository.User.AddressRepository;
import com.example.backend.database.repository.User.UserRepository;
import com.example.backend.dto.API.AType;
import com.example.backend.dto.API.ApiType;
import com.example.backend.dto.Request.User.UpdateProfile;
import com.example.backend.dto.Response.Other.CloudinaryResponse;
import com.example.backend.service.Other.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AType updateProfile(String currentUsername, UpdateProfile request) {

        // 1. Fetch data
        Account account = accountRepository.findByUserName(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + currentUsername));

        User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("User profile not found for: " + currentUsername));

        Address address = addressRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Address not found for user: " + user.getUserID()));

        // 2. Update Account (Cẩn thận với việc đổi username)
        if (request.getUsername() != null && !request.getUsername().equals(currentUsername)) {
            if (accountRepository.existsByUserName(request.getUsername())) {
                throw new RuntimeException("Username already exists!");
            }
            account.setUserName(request.getUsername());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getEmail() != null) {

            account.setEmail(request.getEmail());

        }

        if (request.getPhone() != null) {

            account.setPhone(request.getPhone());

        }

        // 3. Update User Info
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {

            // 4. Remove old avatar
            if(!user.getAvatarID().equals("DEFAULT_AVATAR")) {
                boolean isDeleted = cloudinaryService.deleteFile(user.getAvatarID());
                if(!isDeleted) {
                    throw new RuntimeException("Avatar can not deleted !");
                }
            }

            // 5. Upload new avatar
            CloudinaryResponse result = cloudinaryService.uploadImage(request.getAvatar());
            user.setAvatar(result.getUrl());
            user.setAvatarID(result.getPublicId());
        }

        // 6. set user info update
        Optional.ofNullable(request.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(request.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(request.getGender()).ifPresent(user::setGender);
        Optional.ofNullable(request.getBirthday()).ifPresent(user::setDateOfBirth);

        // 7. set  Address Info update
        Optional.ofNullable(request.getProvince()).ifPresent(address::setProvince);
        Optional.ofNullable(request.getDistrict()).ifPresent(address::setDistrict);
        Optional.ofNullable(request.getWard()).ifPresent(address::setWard);
        Optional.ofNullable(request.getStreet()).ifPresent(address::setStreet);

        return ApiType.builder()
                .code(200)
                .message("Update profile successfully!")
                .data(true)
                .build();
    }

}
