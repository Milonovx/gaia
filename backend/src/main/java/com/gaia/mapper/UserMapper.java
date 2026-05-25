package com.gaia.mapper;

import com.gaia.dto.user.UserResponse;
import com.gaia.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNombre(),
                user.getApellido(),
                user.getEmail(),
                user.getTelefono(),
                user.getFotoPerfil(),
                user.getRole(),
                user.getFechaCreacion()
        );
    }
}
