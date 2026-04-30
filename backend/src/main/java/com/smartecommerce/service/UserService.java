package com.smartecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartecommerce.dto.*;
import com.smartecommerce.entity.User;
import com.smartecommerce.entity.UserProfile;

public interface UserService extends IService<User> {
    LoginVO login(LoginDTO dto);
    UserVO register(RegisterDTO dto);
    UserVO getCurrentUser(Long userId);
    UserVO updateProfile(Long userId, UpdateProfileDTO dto);
    UserProfile getUserProfile(Long userId);
    void updateUserProfile(Long userId, UpdateProfileDTO dto);
}
