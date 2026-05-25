package com.gaia.service;

import com.gaia.dto.auth.AuthResponse;
import com.gaia.dto.auth.LoginRequest;
import com.gaia.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
