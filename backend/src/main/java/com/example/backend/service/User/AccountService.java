package com.example.backend.service.User;

import com.example.backend.common.enums.User.UserErrorCode;
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
import com.example.backend.dto.Request.User.ChangePasswordReq;
import com.example.backend.dto.Request.User.ConfirmOTPReq;
import com.example.backend.dto.Request.User.LoginReq;
import com.example.backend.dto.Request.User.RegisterReq;
import com.example.backend.dto.Response.User.LoginRes;
import com.example.backend.exception.UserException;
import com.example.backend.service.Other.MailService;
import com.example.backend.service.Other.RedisService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;

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


    @Transactional
    public ResponseEntity<AType> registerAccount(@NonNull RegisterReq registerReq) {

        // 1.Check email not existed
        if (accountRepository.existsByEmail(registerReq.getEmail())) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. Create new account
        Account account = new Account();
        account.setUserName(UserUtils.generateUniqueUsername());
        account.setEmail(registerReq.getEmail());
        account.setPhone(registerReq.getNumberPhone());
        account.setPasswordHash(passwordEncoder.encode(registerReq.getPassword()));
        account = accountRepository.save(account);

        // 3. Create new user
        User newUser = new User();
        newUser.setFirstName(registerReq.getFirstName());
        newUser.setLastName(registerReq.getLastName());
        newUser.setAccount(account);
        userRepository.save(newUser);

        // 4. Create new address
        Address address = new Address();
        address.setUser(newUser);
        addressRepository.save(address);

        // 4. Create mail token for active new account
        String token = UserUtils.generateUUIDToken();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setAccount(account);
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationRepository.save(verificationToken);

        // 5. Send mail active account
        try {
            mailService.sendMailaActive(account, token);
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
        }

        // 6. Return data for client
        AType result = ApiType.builder()
                .code(200)
                .message("Registration successful. Please check your email to activate account.")
                .data(true)
                .build();

        return ResponseEntity.ok().body(result);
    }

    @Transactional
    public ResponseEntity<AType> confirmActive(@NonNull String token) {

        // 1. Find token in database
        VerificationToken verificationToken = verificationRepository.findByToken(token)
                .orElseThrow(() -> new UserException(UserErrorCode.INVALID_ACTIVE_TOKEN));

        // 2. Throw exception if user not found
        Account account = verificationToken.getAccount();

        if (account == null) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }

        // 3. Update active state
        account.setActive(true);
        accountRepository.save(account);

        // 4. Remove active token after user active accound successfully
        verificationRepository.removeByToken(token);
        AType success = ApiType.builder()
                .code(200)
                .message("Account activated successfully")
                .data(true)
                .build();

        return ResponseEntity.ok().body(success);
    }

    public ResponseEntity<AType> loginAccount(@NonNull LoginReq loginReq) {

        // 1. Check account existed
        Account account = accountRepository.findByEmail(loginReq.getEmail())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 2. Check user was active or baned account
        if (Boolean.FALSE.equals(account.getActive())) {
            throw new UserException(UserErrorCode.ACCOUNT_NOT_ACTIVE);
        }

        if (Boolean.TRUE.equals(account.getBanned())) {
            throw new UserException(UserErrorCode.ACCOUNT_IS_BANED);
        }

        // 3. Check password is match
        if (!passwordEncoder.matches(loginReq.getPassword(), account.getPasswordHash())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        // 4. Sign access token
        String accessToken = jwtService.generateToken(account, "ACCESS");

        // 5. Find old refresh token if it has in DB
        ExistingTokens existingToken = tokenRepository.findByAccount(account)
                .orElse(new ExistingTokens());

        String refreshToken;

        // 6. if refresh token not exist in DB , create new existing token and save to DB
        if (existingToken.getToken() == null || jwtService.validateToken(existingToken.getToken(), "REFRESH")) {

            refreshToken = jwtService.generateToken(account, "REFRESH");
            existingToken.setAccount(account);
            existingToken.setToken(refreshToken);
            existingToken.setTokenType("REFRESH");
            tokenRepository.save(existingToken);

        } else {
            refreshToken = existingToken.getToken();
        }

        // 7. Get user info in DB
        User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 8. Return data for client
        LoginRes res = LoginRes.builder()
                .AccessToken(accessToken)
                .RefreshToken(refreshToken)
                .FirstName(user.getFirstName())
                .LastName(user.getLastName())
                .UserName(account.getUserName())
                .AvatarUrl(user.getAvatar())
                .Email(account.getEmail())
                .build();

        return ResponseEntity.ok(ApiType.<LoginRes>builder()
                .code(200)
                .message("Login successful")
                .data(res)
                .build());
    }

    public ResponseEntity<AType> logoutAccount(@NonNull String token) {


        // 1. check token is refresh token and token don't expired
        boolean isValid = jwtService.validateToken(token,"REFRESH");
        log.info("authentication status : {}", isValid);

        if (!isValid) {
            throw new UserException(UserErrorCode.VARIFY_FAILED);
        }

        // 2. Get Existing Token from DB
        ExistingTokens existingToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new UserException(UserErrorCode.INVALID_TOKEN));

        // 3. Delete token , prevent user logout too much
        tokenRepository.delete(existingToken);

        // 4. Return result
        AType success = ApiType.builder()
                .code(200)
                .message("Logout successful")
                .data(true)
                .build();

        return ResponseEntity.ok().body(success);
    }

    public ResponseEntity<AType> sendOTP(@NonNull String email) {

        // 1. Check has OTP key in redis
        String cachingKey = "OTP:" + email;

        boolean hasKey = redisService.isCaching(cachingKey);

        if (hasKey) {
            throw new UserException(UserErrorCode.OTP_EXPIRED);
        }

        // 2. Check user exists
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 3. Create random OTP
        String otp = String.valueOf(new Random().nextInt(899999) + 100000);

        // 4. Send OTP via MAIL
        mailService.sendMailOTP(account.getUserName(), email, otp);

        // 5. Save OTP to Redis with TTL is 5 minutes
        redisService.addData(cachingKey, otp, 5, TimeUnit.MINUTES);

        // 6. Return result
        AType success = ApiType.builder()
                .code(200)
                .message("OTP sent successfully")
                .data(true)
                .build();

        return ResponseEntity.ok().body(success);
    }

    public ResponseEntity<AType> confirmOTP(@NonNull ConfirmOTPReq confirmOTPReq) {

        // 1. Get OTP from Redis
        String optKey = "OTP:" + confirmOTPReq.getEmail();

        String otpExist = redisService.getData(optKey);

        // 2. Check OTP existed
        if (otpExist == null) {
            throw new UserException(UserErrorCode.NOT_FOUND_OTP);
        }

        // 3. Check OTP matched
        if (!otpExist.equals(confirmOTPReq.getOtp())) {
            throw new UserException(UserErrorCode.OTP_NOT_MATCH);
        }

        // 4. return result for user
        redisService.removeData(optKey);
        AType success = ApiType.builder()
                .code(200)
                .message("OTP confirmed successfully")
                .data(true)
                .build();
        return ResponseEntity.ok().body(success);
    }

    public ResponseEntity<AType> changePassword(ChangePasswordReq req) {
        // 1. Check OTP is existed Redis
        String cachingKey = "OTP:" + req.getEmail();

        boolean hasKey = redisService.isCaching(cachingKey);
        if (hasKey) {
            throw new UserException(UserErrorCode.OTP_EXPIRED);
        }

        // 2.Check user existed
        Account account = accountRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 3. Compare password
        String oldPassword = req.getPassword();
        String newPassword = req.getConfirmPassword();

        if (!oldPassword.equals(newPassword)) {
            throw new UserException(UserErrorCode.PASSWORD_NOT_MATCH);
        }

        // 4. Save new password and return result
        account.setPasswordHash(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        AType success = ApiType.builder()
                .code(200)
                .message("Password changed successfully")
                .data(true)
                .build();

        return ResponseEntity.ok(success);
    }

    public ResponseEntity<AType> refreshToken(@NotEmpty String token) {

        // 1. check token is refresh token and token don't expired
        boolean isValid = jwtService.validateToken(token,"REFRESH");

        if (isValid) {
            throw new UserException(UserErrorCode.INVALID_TOKEN);
        }

        String username = jwtService.extractUsername(token);

        // 2. Check user existed
        Account account = accountRepository.findByUserName(username).orElseThrow(
                () -> new UserException(UserErrorCode.USER_NOT_FOUND)
        );

        // 3. Sign access token and return result
        String accessToken = jwtService.generateToken(account,"ACCESS");

        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Token refreshed successfully")
                .data(accessToken)
                .build());

    }

    // ADMIN API

    private ResponseEntity<AType> executeBulkUpdate(
            List<Integer> accountIDs,
            String successMessage,
            IntSupplier updateLogic
    ) {
        // 1. Flash check user in list exist
        long count = accountRepository.countByAccountIDIn(accountIDs);
        if (count == 0) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }

        // 2. Execute update logic
        int result = updateLogic.getAsInt();

        if (result == 0) {
            throw new UserException(UserErrorCode.UPDATE_USER_FAILED);
        }

        // 3. Return data with ApiType format
        AType success = ApiType.builder()
                .code(200)
                .message(successMessage + " for " + result + "/" + accountIDs.size() + " account")
                .data(true)
                .build();

        return ResponseEntity.ok(success);
    }

    public ResponseEntity<AType> resetPassword(@NotEmpty List<Integer> ids) {
        String hashPassword = passwordEncoder.encode("furniro2026");
        return executeBulkUpdate(ids, "Reset password", () -> accountRepository.resetPasswords(ids, hashPassword));
    }

    public ResponseEntity<AType> banAccount(@NotEmpty List<Integer> ids) {
        return executeBulkUpdate(ids, "Ban account", () -> accountRepository.banAccounts(ids));
    }

    public ResponseEntity<AType> unbanAccount(@NotEmpty List<Integer> ids) {
        return executeBulkUpdate(ids, "Unban account", () -> accountRepository.unbanAccounts(ids));
    }

    public ResponseEntity<AType> deleteAccount(@NotEmpty List<Integer> ids) {
        return executeBulkUpdate(ids, "Delete account", () -> accountRepository.deleteAccounts(ids));
    }
}
