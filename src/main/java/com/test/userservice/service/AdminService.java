package com.test.userservice.service;

import com.test.userservice.dto.RegisterUserRequestDTO;
import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<?> registerUser(RegisterUserRequestDTO registerUserRequestDTO);
    ResponseEntity<?> getUserList();
    ResponseEntity<?> deleteUser(String username);
}
