package org.example.onepiece.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.onepiece.dto.Result;
import org.example.onepiece.service.ICartService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/cart")
public class CartController {

    @Resource
    private ICartService cartService;

    @PostMapping("/add")
    public Result add(@RequestBody Map<String, Object> body) {
        Long shopId = Long.valueOf(body.get("shopId").toString());
        Integer num = body.containsKey("num") ? Integer.valueOf(body.get("num").toString()) : 1;
        return cartService.addToCart(shopId, num);
    }

    @GetMapping("/list")
    public Result list() {
        return cartService.listCart();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        Integer num = Integer.valueOf(body.get("num").toString());
        return cartService.updateNum(id, num);
    }

    @DeleteMapping("/{id}")
    public Result remove(@PathVariable Long id) {
        return cartService.removeFromCart(id);
    }
}
