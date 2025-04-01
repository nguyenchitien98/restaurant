package com.tien.mapper;

import com.tien.dto.request.UserRequest;
import com.tien.dto.response.UserResponse;
import com.tien.model.User;

public class UserMapper {
    public static User mapToUser(UserRequest userRequest) {
        User user = new User(
                userRequest.getUserId(),
                userRequest.getUsername(),
                userRequest.getPassword(),
                userRequest.getEmail(),
                "USER"
        );
        return user;
    }

    public static UserResponse mapToUserResponse(User user) {
        UserResponse userResponse = new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
        return userResponse;
    }
}
