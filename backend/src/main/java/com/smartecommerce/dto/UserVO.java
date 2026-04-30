package com.smartecommerce.dto;

import com.smartecommerce.entity.User;
import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
}
