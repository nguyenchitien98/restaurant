package com.tien.service;

import com.tien.dto.request.UserRequest;
import com.tien.dto.response.UserResponse;
import com.tien.mapper.UserMapper;
import com.tien.model.User;
import com.tien.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public UserResponse createUser(UserRequest userRequest) {
        User user = UserMapper.mapToUser(userRequest);
        User savedUser = userRepository.save(user);

        return UserMapper.mapToUserResponse(savedUser);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return UserMapper.mapToUserResponse(user);
    }
}
