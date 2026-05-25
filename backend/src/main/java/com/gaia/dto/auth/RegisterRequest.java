package com.gaia.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 80) String nombre,
        @NotBlank @Size(max = 80) String apellido,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(min = 8, max = 120) String password,
        @Size(max = 30) String telefono
) {
}
