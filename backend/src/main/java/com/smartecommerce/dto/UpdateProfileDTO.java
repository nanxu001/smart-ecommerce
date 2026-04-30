package com.smartecommerce.dto;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private String gender;
    private String ageRange;
    private String budgetLevel;
    private String preferredCategories;
    private String tags;
}
