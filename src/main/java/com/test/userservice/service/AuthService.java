package com.test.userservice.service;

import com.test.userservice.dto.LoginRequestDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginRequestDTO loginRequestDTO);
    ResponseEntity<?> logout();
    ResponseEntity<?> fetchMe();
}
