package com.smartecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartecommerce.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {

    @Select("SELECT DISTINCT product_id FROM user_behaviors WHERE user_id = #{userId} AND type = 'view' ORDER BY created_at DESC LIMIT #{limit}")
    List<Long> selectRecentViewedProducts(@Param("userId") Long userId, @Param("limit") int limit);

    @Select("SELECT product_id, type, COUNT(*) as cnt FROM user_behaviors WHERE user_id = #{userId} GROUP BY product_id, type")
    List<UserBehaviorStats> selectUserBehaviorStats(@Param("userId") Long userId);

    interface UserBehaviorStats {
        Long getProductId();
        String getType();
        Integer getCnt();
    }
}
