package com.gaia.controller;

import com.gaia.dto.user.UpdateUserProfileRequest;
import com.gaia.dto.user.UpdateUserRoleRequest;
import com.gaia.dto.user.UserResponse;
import com.gaia.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Consultar perfil autenticado")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Principal principal) {
        return ResponseEntity.ok(userService.me(principal.getName()));
    }

    @Operation(summary = "Actualizar perfil autenticado")
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            @Valid @RequestBody UpdateUserProfileRequest request,
            Principal principal
    ) {
        return ResponseEntity.ok(userService.updateMe(request, principal.getName()));
    }

    @Operation(summary = "Listar usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Actualizar rol de usuario")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request,
            Principal principal
    ) {
        return ResponseEntity.ok(userService.updateRole(id, request, principal.getName()));
    }

    @Operation(summary = "Eliminar usuario")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        userService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
