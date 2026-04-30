package com.smartecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_behaviors")
public class UserBehavior {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long productId;
    private String type;
    private String searchQuery;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
