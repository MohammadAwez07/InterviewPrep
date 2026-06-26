package com.interviewprep.auth.service;

import com.interviewprep.auth.dto.AuthResponse;
import com.interviewprep.auth.dto.LoginRequest;
import com.interviewprep.auth.dto.RegisterRequest;
import com.interviewprep.auth.jwt.JwtTokenProvider;
import com.interviewprep.auth.model.User;
import com.interviewprep.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = User.builder()
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .fullName(req.fullName())
                .build();
        user = userRepository.save(user);
        String token = jwtTokenProvider.generateTokenForEmail(user.getEmail());
        return AuthResponse.of(token, user.getId(), user.getEmail(), user.getFullName());
    }

    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        User user = (User) auth.getPrincipal();
        String token = jwtTokenProvider.generateToken(auth);
        return AuthResponse.of(token, user.getId(), user.getEmail(), user.getFullName());
    }
}
