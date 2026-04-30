package com.smartecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartecommerce.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}
