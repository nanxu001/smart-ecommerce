package com.smartecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_profiles")
public class UserProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String gender;
    private String ageRange;
    private String budgetLevel;
    private String preferredCategories;
    private String tags;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastUpdated;
}
