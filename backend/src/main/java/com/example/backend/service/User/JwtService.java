package com.example.backend.service.User;

import com.example.backend.database.entity.User.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    @Value("${JWT_ACCESS_EXPIRATION}")
    private long accessExpiration;

    @Value("${JWT_REFRESH_EXPIRATION}")
    private long refreshExpiration;


    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(Account account, String tokenType) {
        // Xác định thời gian hết hạn dựa trên loại token
        long expirationTime = tokenType.equalsIgnoreCase("ACCESS")
                ? accessExpiration
                : refreshExpiration;

        return Jwts.builder()
                .subject(account.getUserName()) // Đảm bảo đúng getter của bạn
                .claim("role", account.getRole())
                .claim("type", tokenType)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token, String tokenType) {
        try {
            // Nếu token hết hạn hoặc sai signature, dòng dưới đây sẽ văng Exception ngay
            Claims claims = extractAllClaims(token);

            boolean isCorrectType = claims.get("type").toString().equalsIgnoreCase(tokenType);
            boolean isNotExpired = !claims.getExpiration().before(new Date());

            return isCorrectType && isNotExpired;
        } catch (Exception e) {
            // Nếu có bất kỳ lỗi nào (hết hạn, sai key, token rác), trả về false luôn cho an toàn
            return false;
        }
    }
    // 6. Parse Claims (Cách dùng Parser mới)
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}