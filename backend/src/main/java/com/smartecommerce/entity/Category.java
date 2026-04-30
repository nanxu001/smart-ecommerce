package com.smartecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("categories")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long parentId;
    private String icon;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
