package com.tien.service;

import com.tien.dto.request.UserRequest;
import com.tien.dto.response.UserResponse;
import com.tien.mapper.UserMapper;
import com.tien.model.User;
import com.tien.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;



    public UserResponse getUserById(Long userId) {
        User user = userRepository.findByUserId(userId);
        return UserMapper.mapToUserResponse(user);
    }



    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return UserMapper.mapToUserResponse(user);
    }

    public List<UserResponse> getAllUser() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(UserMapper::mapToUserResponse)
                .collect(Collectors.toList());
        return users;
    }
}
