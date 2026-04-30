package com.smartecommerce.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.smartecommerce.common.Result;
import com.smartecommerce.entity.Category;
import com.smartecommerce.entity.Product;
import com.smartecommerce.service.ProductService;
import com.smartecommerce.service.BehaviorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final BehaviorService behaviorService;

    @GetMapping("/products")
    public Result<IPage<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sortBy) {
        return Result.success(productService.getProducts(page, size, keyword, categoryId, sortBy));
    }

    @GetMapping("/products/{id}")
    public Result<Product> detail(@PathVariable Long id, HttpServletRequest request) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        // 记录浏览行为
        Long userId = (Long) request.getAttribute("userId");
        if (userId != null) {
            behaviorService.recordBehavior(userId, id, "view", null);
        }
        return Result.success(product);
    }

    @GetMapping("/products/hot")
    public Result<List<Product>> hot(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(productService.getHotProducts(limit));
    }

    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        return Result.success(productService.getCategories());
    }

    @PostMapping("/products")
    public Result<Product> create(@RequestBody Product product, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        productService.save(product);
        return Result.success(product);
    }

    @PutMapping("/products/{id}")
    public Result<Product> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        productService.updateById(product);
        return Result.success(product);
    }

    @DeleteMapping("/products/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.removeById(id);
        return Result.success();
    }
}
