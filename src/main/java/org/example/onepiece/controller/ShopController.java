package org.example.onepiece.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.onepiece.dto.Result;
import org.example.onepiece.service.IShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/shop")
public class ShopController {
    @Resource
    private IShopService shopService;
    @GetMapping("")
    public Result queryAll(
            @RequestParam(value = "typeId", required = false) String typeId,
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return shopService.queryByPage(current, size, typeId);
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
