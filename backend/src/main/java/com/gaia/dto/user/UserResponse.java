package com.gaia.dto.user;

import com.gaia.entity.Role;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String fotoPerfil,
        Role role,
        LocalDateTime fechaCreacion
) {
}
