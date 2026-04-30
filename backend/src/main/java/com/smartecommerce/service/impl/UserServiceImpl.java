package com.smartecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartecommerce.dto.*;
import com.smartecommerce.entity.User;
import com.smartecommerce.entity.UserProfile;
import com.smartecommerce.mapper.UserMapper;
import com.smartecommerce.mapper.UserProfileMapper;
import com.smartecommerce.service.UserService;
import com.smartecommerce.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserProfileMapper userProfileMapper;
    private final JwtUtils jwtUtils;

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String encryptedPassword = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new RuntimeException("密码错误");
        }
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return new LoginVO(token, vo);
    }

    @Override
    @Transactional
    public UserVO register(RegisterDTO dto) {
        // 检查用户名是否已存在
        long count = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes(StandardCharsets.UTF_8)));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        save(user);

        // 创建默认用户画像
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setPreferredCategories("[]");
        profile.setTags("[]");
        userProfileMapper.insert(profile);

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    @Transactional
    public UserVO updateProfile(Long userId, UpdateProfileDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        updateById(user);

        // 更新用户画像
        UserProfile profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId));
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
        }
        if (dto.getGender() != null) profile.setGender(dto.getGender());
        if (dto.getAgeRange() != null) profile.setAgeRange(dto.getAgeRange());
        if (dto.getBudgetLevel() != null) profile.setBudgetLevel(dto.getBudgetLevel());
        if (dto.getPreferredCategories() != null) profile.setPreferredCategories(dto.getPreferredCategories());
        if (dto.getTags() != null) profile.setTags(dto.getTags());

        if (profile.getId() == null) {
            userProfileMapper.insert(profile);
        } else {
            userProfileMapper.updateById(profile);
        }

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public UserProfile getUserProfile(Long userId) {
        return userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId));
    }

    @Override
    public void updateUserProfile(Long userId, UpdateProfileDTO dto) {
        UserProfile profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId));
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setPreferredCategories("[]");
            profile.setTags("[]");
            userProfileMapper.insert(profile);
        }
        if (dto.getGender() != null) profile.setGender(dto.getGender());
        if (dto.getAgeRange() != null) profile.setAgeRange(dto.getAgeRange());
        if (dto.getBudgetLevel() != null) profile.setBudgetLevel(dto.getBudgetLevel());
        if (dto.getPreferredCategories() != null) profile.setPreferredCategories(dto.getPreferredCategories());
        if (dto.getTags() != null) profile.setTags(dto.getTags());
        userProfileMapper.updateById(profile);
    }
}
