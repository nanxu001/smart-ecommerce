package com.smartecommerce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smartecommerce.entity.Product;
import com.smartecommerce.entity.Category;

import java.util.List;

public interface ProductService extends IService<Product> {
    IPage<Product> getProducts(int page, int size, String keyword, Long categoryId, String sortBy);
    List<Product> getHotProducts(int limit);
    List<Category> getCategories();
    List<Product> getRelatedProducts(Long productId, int limit);
}
