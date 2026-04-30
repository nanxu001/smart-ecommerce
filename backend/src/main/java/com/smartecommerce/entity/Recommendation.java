package com.smartecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("recommendations")
public class Recommendation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String type;
    private String queryText;
    private String productIds;
    private LocalDateTime expiresAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
