package com.gaia.service;

import com.gaia.dto.user.UpdateUserProfileRequest;
import com.gaia.dto.user.UpdateUserRoleRequest;
import com.gaia.dto.user.UserResponse;
import java.util.List;

public interface UserService {

    UserResponse me(String email);

    UserResponse updateMe(UpdateUserProfileRequest request, String email);

    List<UserResponse> findAll();

    UserResponse updateRole(Long id, UpdateUserRoleRequest request, String authenticatedEmail);

    void delete(Long id, String authenticatedEmail);
}
