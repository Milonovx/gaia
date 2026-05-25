package com.gaia.dto.auth;

import com.gaia.dto.user.UserResponse;

public record AuthResponse(
        String token,
        String tokenType,
        UserResponse usuario
) {
}
