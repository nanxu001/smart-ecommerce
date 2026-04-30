package com.smartecommerce.controller;

import com.smartecommerce.common.Result;
import com.smartecommerce.dto.*;
import com.smartecommerce.entity.UserProfile;
import com.smartecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO dto) {
        try {
            return Result.success(userService.register(dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        try {
            return Result.success(userService.login(dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/me")
    public Result<UserVO> me(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getCurrentUser(userId));
    }

    @PutMapping("/profile")
    public Result<UserVO> updateProfile(HttpServletRequest request, @RequestBody UpdateProfileDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.updateProfile(userId, dto));
    }

    @GetMapping("/profile")
    public Result<UserProfile> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getUserProfile(userId));
    }
}
