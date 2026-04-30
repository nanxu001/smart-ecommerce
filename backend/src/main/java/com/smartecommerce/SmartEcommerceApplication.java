package com.smartecommerce;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.smartecommerce.mapper")
public class SmartEcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartEcommerceApplication.class, args);
    }
}
