package com.tien.controller;

import com.tien.dto.request.LoginRequest;
import com.tien.dto.request.UserRequest;
import com.tien.dto.response.UserResponse;
import com.tien.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest user) {
        return new ResponseEntity<>(authService.createUser(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginUser(@RequestBody LoginRequest request) {
        // Logic login (sẽ làm tiếp trong phần xác thực)
        UserResponse user = authService.getUserByUsername(request.getUsername());
        return ResponseEntity.ok(user);
    }
}
