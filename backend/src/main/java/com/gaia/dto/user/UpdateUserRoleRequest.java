package com.gaia.dto.user;

import com.gaia.entity.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
        @NotNull Role role
) {
}
