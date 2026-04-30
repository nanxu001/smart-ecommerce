package com.smartecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartecommerce.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
