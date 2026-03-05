package com.example.backend.service.User;

import com.example.backend.common.utils.UserUtils;
import com.example.backend.database.entity.Email.VerificationToken;
import com.example.backend.database.entity.User.Account;
import com.example.backend.database.entity.User.Address;
import com.example.backend.database.entity.User.ExistingTokens;
import com.example.backend.database.entity.User.User;
import com.example.backend.database.repository.Mail.VerificationRepository;
import com.example.backend.database.repository.User.AccountRepository;
import com.example.backend.database.repository.User.AddressRepository;
import com.example.backend.database.repository.User.TokenRepository;
import com.example.backend.database.repository.User.UserRepository;
import com.example.backend.dto.API.AType;
import com.example.backend.dto.API.ApiType;
import com.example.backend.dto.API.ErrorType;
import com.example.backend.dto.Request.User.ChangePasswordReq;
import com.example.backend.dto.Request.User.LoginReq;
import com.example.backend.dto.Request.User.RegisterReq;
import com.example.backend.dto.Response.User.LoginRes;
import com.example.backend.service.Other.MailService;
import com.example.backend.service.Other.RedisService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor

public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final VerificationRepository verificationRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final RedisService redisService;


    @Transactional // Đảm bảo tính toàn vẹn dữ liệu
    public AType registerAccount(@NonNull RegisterReq registerReq) {
        // 1. Kiểm tra Email tồn tại
        if (accountRepository.existsByEmail(registerReq.getEmail())) {
            return ErrorType.builder()
                    .code(400)
                    .message("Your email address is already in use.")
                    .build();
        }

        // 2. Tạo Account (Thông tin đăng nhập)
        Account account = new Account();
        account.setUserName(UserUtils.generateUniqueUsername());
        account.setEmail(registerReq.getEmail());
        account.setPhone(registerReq.getNumberPhone());
        account.setPasswordHash(passwordEncoder.encode(registerReq.getPassword()));
        account = accountRepository.save(account);

        // 3. Tạo User (Thông tin cá nhân) & Address
        User newUser = new User();
        newUser.setFirstName(registerReq.getFirstName());
        newUser.setLastName(registerReq.getLastName());
        newUser.setAccount(account);
        userRepository.save(newUser);

        Address address = new Address();
        address.setUser(newUser);
        addressRepository.save(address);

        // 4. Tạo Token kích hoạt
        String token = UserUtils.generateUUIDToken();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setAccount(account);
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationRepository.save(verificationToken);

        // 5. Gửi Mail (Hãy đánh dấu @Async trong mailService.sendMail)
        try {
            mailService.sendMailaActive(account, token);
        } catch (Exception e) {
            // Log lỗi nhưng có thể cân nhắc cách xử lý nếu mail server chết
            log.error("Failed to send email: {}", e.getMessage());
        }

        return ApiType.builder()
                .code(200)
                .message("Registration successful. Please check your email to activate account.")
                .data(true)
                .build();
    }

    @Transactional // Quan trọng nhất: Giúp thực thi lệnh save và remove trong một giao dịch
    public AType confirmActive(@NonNull String token) {
        // 1. Tìm token
        VerificationToken verificationToken = verificationRepository.findByToken(token)
                .orElse(null);

        if (verificationToken == null) {
            return ErrorType.builder().code(400).message("Invalid token").build();
        }

        // 2. Lấy Account trực tiếp từ token (vì bạn đã dùng @OneToOne)
        Account account = verificationToken.getAccount();
        if (account == null) {
            return ErrorType.builder().code(400).message("Account not found").build();
        }

        // 3. Cập nhật trạng thái
        account.setActive(true);
        accountRepository.save(account);

        // 4. Xóa token (Thao tác này gây ra lỗi nếu thiếu @Transactional)
        verificationRepository.removeByToken(token);

        return ApiType.builder()
                .code(200)
                .message("Account activated successfully")
                .data(true)
                .build();
    }

    public ApiType<LoginRes> loginAccount(@NonNull LoginReq loginReq) {
        // 1. Tìm Account
        Account account = accountRepository.findByEmail(loginReq.getEmail())
                .orElse(null);

        if (account == null) {
            return ApiType.<LoginRes>builder().code(401).message("Invalid email or password").build();
        }

        // 2. Kiểm tra trạng thái tài khoản
        if (Boolean.FALSE.equals(account.getActive())) {
            return ApiType.<LoginRes>builder().code(403).message("Account is not active").build();
        }

        if (Boolean.TRUE.equals(account.getBanned())) {
            return ApiType.<LoginRes>builder().code(403).message("Account is banned").build();
        }

        // 3. Kiểm tra Password
        boolean isMatch = passwordEncoder.matches(loginReq.getPassword(), account.getPasswordHash());
        if (!isMatch) {
            return ApiType.<LoginRes>builder().code(401).message("Invalid email or password").build();
        }

        // 4. Tạo Access Token (Luôn tạo mới mỗi lần login)
        String accessToken = jwtService.generateToken(account, "ACCESS");

        // 5. Xử lý Refresh Token (Chỉ tạo mới khi cái cũ không dùng được nữa)
        String refreshToken;
        ExistingTokens existingToken = tokenRepository.findByAccount(account).orElse(null);

        // LOGIC: Nếu CHƯA CÓ hoặc (CÓ nhưng ĐÃ HẾT HẠN hoặc BỊ HỦY) thì mới tạo mới
        if (existingToken == null || existingToken.isExpired() || existingToken.isRevoked()) {
            refreshToken = jwtService.generateToken(account, "REFRESH");

            // Cập nhật hoặc tạo mới bản ghi Token trong DB
            if (existingToken == null) {
                existingToken = new ExistingTokens();
                existingToken.setAccount(account);
            }

            existingToken.setToken(refreshToken);
            existingToken.setTokenType("REFRESH");
            existingToken.setRevoked(false);
            existingToken.setExpired(false);
            // Lưu ý: Cần setExpiryDate khớp với logic của JWT (vd: 7 ngày sau)
            existingToken.setExpiryDate(LocalDateTime.now().plusDays(7));

            tokenRepository.save(existingToken);
        } else {
            // Nếu còn dùng được, lấy token cũ trả về luôn
            refreshToken = existingToken.getToken();
        }

        // 6. Lấy thông tin User (Profile)
        User user = userRepository.findByAccount(account);

        // 7. Trả về kết quả
        LoginRes res = LoginRes.builder()
                .AccessToken(accessToken)
                .RefreshToken(refreshToken)
                .FirstName(user.getFirstName())
                .LastName(user.getLastName())
                .UserName(account.getUserName())
                .AvatarUrl(user.getAvatar())
                .Email(account.getEmail())
                .build();

        return ApiType.<LoginRes>builder()
                .code(200)
                .message("Login successful")
                .data(res)
                .build();
    }

    public AType logoutAccount(@NonNull String token) {
        log.info(token);
        ExistingTokens existingToken = tokenRepository.findByToken(token)
                .orElse(null);

        if (existingToken == null) {
            return ErrorType.builder().code(400).message("Invalid token").build();
        }

        existingToken.setRevoked(true);
        existingToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(existingToken);

        return ApiType.builder()
                .code(200)
                .message("Logout successful")
                .data(true)
                .build();
    }

    public AType sendOTP(@NonNull String email) {
        // kiem tra user ton tai

        Account account = accountRepository.findByEmail(email)
                .orElse(null);

        if (account == null) {
            return ErrorType.builder().code(400).message("Invalid email").build();
        }

        // tạo opt
        String otp = String.valueOf(new Random().nextInt(899999) + 100000);
        String cachingKey = "Account-OPT" + otp;

        //send mail otp
        mailService.sendMailOTP(account.getUserName(),email,otp);

        // luu caching vao redis
        redisService.addData(cachingKey, otp);

        return ApiType.builder()
                .code(200)
                .message("OTP sent successfully")
                .data(true)
                .build();

    }

    public AType confirmOTP(@NonNull String otp) {

        String optKey = "Account-OPT" + otp;

        String otpExist = redisService.getData(optKey);

        if (otpExist == null) {
            return ErrorType.builder().code(400).message("Invalid OTP").build();
        }


        if (!otp.equals(otpExist)) {
            return ErrorType.builder().code(400).message("OTP is not match").build();
        }

        redisService.removeData(optKey);
        return ApiType.builder()
                .code(200)
                .message("OTP confirmed successfully")
                .data(true)
                .build();
    }

    public AType changePassword(ChangePasswordReq req) {

        Account account = accountRepository.findByEmail(req.getEmail())
                .orElse(null);

        if (account == null) {
            return ErrorType.builder().code(400).message("User not existed").build();
        }

        String oldPassword = req.getOldPassword();
        String newPassword = req.getNewPassword();

        if (!oldPassword.equals(newPassword)) {
            return ErrorType.builder().code(400).message("Passwords don't match").build();
        }

        account.setPasswordHash(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        return ApiType.builder()
                .code(200)
                .message("Password changed successfully")
                .data(true)
                .build();
    }

    public AType resetPassword (@NotEmpty int AccountID) {
        Account account = accountRepository.findById(AccountID).orElse(null);

        if (account == null) {
            return ErrorType.builder().code(400).message("Account not existed").build();
        }
        account.setPasswordHash(passwordEncoder.encode("furniro2026"));
        accountRepository.save(account);

        return ApiType.builder()
                .code(200)
                .message("User"+ account.getUserName() +" Deleted successfully")
                .data(true)
                .build();
    }

    public AType banAccount(@NotEmpty int AccountID) {
        Account account = accountRepository.findById(AccountID).orElse(null);

        if (account == null) {
            return ErrorType.builder().code(400).message("Account not existed").build();
        }

        account.setBanned(true);
        accountRepository.save(account);

        return ApiType.builder()
                .code(200)
                .message("Account banned successfully")
                .data(true)
                .build();
    }
}
