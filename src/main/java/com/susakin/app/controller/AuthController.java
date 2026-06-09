package com.susakin.app.controller;

import com.susakin.app.dto.req.auth.LoginReq;
import com.susakin.app.dto.req.auth.RegisterReq;
import com.susakin.app.dto.res.auth.AuthRes;
import com.susakin.app.dto.res.common.ApiResponse;
import com.susakin.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterReq request) {
        authService.register(request);
        return ApiResponse.ok("Đăng ký thành công");
    }

    @PostMapping("/login")
    public ApiResponse<AuthRes> login(@Valid @RequestBody LoginReq request) {
        return ApiResponse.ok("Đăng nhập thành công", authService.login(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        authService.logout(token);
        return ApiResponse.ok("Đăng xuất thành công");
    }
}
