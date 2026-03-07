package com.example.backend.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Declare public url
    private static final String[] WHITE_LIST_URLS = {
            "/account/login",
            "/account/register",
            "/account/sendOTP",
            "/account/confirmOTP",
            "/account/refresh",
            "/account/logout",
            "/account/changePassword",
            "/account/confirm/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger-resources/**"
    };

    private final JwtAuthenticationFilter jwtAuthFilter;


    @Bean
    // Create hash password utils
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    // Manage spring security filter chain request before request navigate to controller
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Config CORS for frontend connect
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))

                // dísable csrf
                .csrf(AbstractHttpConfigurer::disable)

                // control access url public / private
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST_URLS).permitAll()
                        .anyRequest().authenticated()
                )

                // handle exception authorize
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Lỗi 401: Chưa đăng nhập hoặc Token lởm
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized - Please login");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Lỗi 403: Đã login nhưng không đủ quyền (Ví dụ: Role USER vào vùng ADMIN)
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden - You don't have permission");
                        })
                )
                // filter request to jwt authentication
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // manage session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

}
