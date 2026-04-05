package org.example.onepiece.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.onepiece.dto.Result;
import org.example.onepiece.entity.Cart;

public interface ICartService extends IService<Cart> {
    Result addToCart(Long shopId, Integer num);
    Result listCart();
    Result updateNum(Long id, Integer num);
    Result removeFromCart(Long id);
}
