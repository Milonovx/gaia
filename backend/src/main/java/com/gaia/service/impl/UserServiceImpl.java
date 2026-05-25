package com.gaia.service.impl;

import com.gaia.dto.user.UpdateUserProfileRequest;
import com.gaia.dto.user.UpdateUserRoleRequest;
import com.gaia.dto.user.UserResponse;
import com.gaia.entity.User;
import com.gaia.exception.ApiException;
import com.gaia.exception.ResourceNotFoundException;
import com.gaia.mapper.UserMapper;
import com.gaia.repository.UserRepository;
import com.gaia.service.UserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse me(String email) {
        return userMapper.toResponse(findByEmail(email));
    }

    @Override
    @Transactional
    public UserResponse updateMe(UpdateUserProfileRequest request, String email) {
        User user = findByEmail(email);
        user.setNombre(request.nombre().trim());
        user.setApellido(request.apellido().trim());
        user.setTelefono(trimToNull(request.telefono()));
        user.setFotoPerfil(trimToNull(request.fotoPerfil()));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAllByOrderByFechaCreacionDesc().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse updateRole(Long id, UpdateUserRoleRequest request, String authenticatedEmail) {
        User user = findById(id);
        if (user.getEmail().equalsIgnoreCase(authenticatedEmail)) {
            throw new ApiException("No puede cambiar su propio rol", HttpStatus.BAD_REQUEST);
        }
        user.setRole(request.role());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long id, String authenticatedEmail) {
        User user = findById(id);
        if (user.getEmail().equalsIgnoreCase(authenticatedEmail)) {
            throw new ApiException("No puede eliminar su propio usuario", HttpStatus.BAD_REQUEST);
        }
        userRepository.delete(user);
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
