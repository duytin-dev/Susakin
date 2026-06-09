package com.susakin.app.service.impl;

import com.susakin.app.config.JwtProperties;
import com.susakin.app.dto.req.auth.LoginReq;
import com.susakin.app.dto.req.auth.RegisterReq;
import com.susakin.app.dto.res.auth.AuthRes;
import com.susakin.app.entity.User;
import com.susakin.app.exception.BadRequestException;
import com.susakin.app.repository.UserRepository;
import com.susakin.app.security.TokenBlacklistService;
import com.susakin.app.security.UserPrincipal;
import com.susakin.app.service.AuthService;
import com.susakin.app.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public AuthRes login(LoginReq request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(userPrincipal);

        return buildAuthResponse(userPrincipal, token);
    }

    @Override
    @Transactional
    public void register(RegisterReq request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setGrade(request.getGrade());

        userRepository.save(user);
    }

    @Override
    public void logout(String token) {
        tokenBlacklistService.blacklist(token, jwtProperties.getExpirationMs());
    }

    private AuthRes buildAuthResponse(UserPrincipal userPrincipal, String token) {
        return AuthRes.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpirationMs() / 1000)
                .user(AuthRes.UserInfo.builder()
                        .id(userPrincipal.getId())
                        .email(userPrincipal.getEmail())
                        .name(userPrincipal.getName())
                        .grade(userPrincipal.getGrade())
                        .build())
                .build();
    }
}
