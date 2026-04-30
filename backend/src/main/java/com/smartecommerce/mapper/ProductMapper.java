package com.smartecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartecommerce.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT * FROM products WHERE status = 1 ORDER BY (sales * 2 + views) DESC LIMIT #{limit}")
    List<Product> selectHotProducts(@Param("limit") int limit);

    @Select("SELECT * FROM products WHERE category_id = #{categoryId} AND id != #{productId} AND status = 1 ORDER BY sales DESC LIMIT #{limit}")
    List<Product> selectRelatedByCategory(@Param("categoryId") Long categoryId, @Param("productId") Long productId, @Param("limit") int limit);

    @Select("SELECT * FROM products WHERE status = 1 AND id != #{excludeId} AND (tags LIKE CONCAT('%', #{tags}, '%')) ORDER BY sales DESC LIMIT #{limit}")
    List<Product> selectByTags(@Param("tags") String tags, @Param("excludeId") Long excludeId, @Param("limit") int limit);

    @Update("UPDATE products SET views = views + 1 WHERE id = #{id}")
    void incrementViews(@Param("id") Long id);
}
