package com.susakin.app.service;

import com.susakin.app.dto.req.auth.LoginReq;
import com.susakin.app.dto.req.auth.RegisterReq;
import com.susakin.app.dto.res.auth.AuthRes;

public interface AuthService {

    AuthRes login(LoginReq request);

    void register(RegisterReq request);

    void logout(String token);
}
