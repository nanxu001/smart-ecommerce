package com.smartecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartecommerce.entity.Category;
import com.smartecommerce.entity.Product;
import com.smartecommerce.mapper.CategoryMapper;
import com.smartecommerce.mapper.ProductMapper;
import com.smartecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public IPage<Product> getProducts(int page, int size, String keyword, Long categoryId, String sortBy) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1); // 只查询上架商品

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or().like(Product::getDescription, keyword)
                    .or().like(Product::getTags, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }

        if ("sales".equals(sortBy)) {
            wrapper.orderByDesc(Product::getSales);
        } else if ("price_asc".equals(sortBy)) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sortBy)) {
            wrapper.orderByDesc(Product::getPrice);
        } else {
            wrapper.orderByDesc(Product::getCreatedAt);
        }

        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public List<Product> getHotProducts(int limit) {
        return productMapper.selectHotProducts(limit);
    }

    @Override
    public List<Category> getCategories() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
    }

    @Override
    public List<Product> getRelatedProducts(Long productId, int limit) {
        Product product = getById(productId);
        if (product == null) return List.of();
        return productMapper.selectRelatedByCategory(product.getCategoryId(), productId, limit);
    }
}
