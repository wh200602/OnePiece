package org.example.onepiece.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.onepiece.dto.Result;
import org.example.onepiece.service.IShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/shop")
public class ShopController {
    @Resource
    private IShopService shopService;
    @GetMapping("")
    public Result queryAll(){
        return shopService.queryByPage(1,20);
    }

    @GetMapping("/{id}")
    public Result queryById(@PathVariable Long id){
        return shopService.queryById(id);
    }
    @GetMapping("/categories")
    public Result queryCategories() {
        // 调用服务层的方法来查询分类
        return shopService.queryCategories();
    }
}
