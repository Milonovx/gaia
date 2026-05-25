package com.gaia.service.impl;

import com.gaia.dto.auth.AuthResponse;
import com.gaia.dto.auth.LoginRequest;
import com.gaia.dto.auth.RegisterRequest;
import com.gaia.entity.Role;
import com.gaia.entity.User;
import com.gaia.exception.ApiException;
import com.gaia.mapper.UserMapper;
import com.gaia.repository.UserRepository;
import com.gaia.security.JwtService;
import com.gaia.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ApiException("El email ya está registrado", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .nombre(request.nombre().trim())
                .apellido(request.apellido().trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.password()))
                .telefono(request.telefono())
                .role(Role.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        return new AuthResponse(token, "Bearer", userMapper.toResponse(savedUser));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password())
        );
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new ApiException("Credenciales inválidas", HttpStatus.UNAUTHORIZED));
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, "Bearer", userMapper.toResponse(user));
    }
}
