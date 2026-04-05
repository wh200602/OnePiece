package org.example.onepiece.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.onepiece.dto.CartVO;
import org.example.onepiece.dto.Result;
import org.example.onepiece.entity.Cart;
import org.example.onepiece.entity.Shop;
import org.example.onepiece.mapper.CartMapper;
import org.example.onepiece.mapper.ShopMapper;
import org.example.onepiece.service.ICartService;
import org.example.onepiece.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

    @Autowired
    private ShopMapper shopMapper;

    @Override
    public Result addToCart(Long shopId, Integer num) {
        Long userId = UserHolder.getUser().getId();

        // 校验商品是否存在
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return Result.fail("商品不存在");
        }

        // 查询是否已在购物车
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("shop_id", shopId);
        Cart existing = this.getOne(wrapper);

        if (existing != null) {
            // 已存在，累加数量
            existing.setNum(existing.getNum() + num);
            this.updateById(existing);
        } else {
            // 不存在，新增
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setShopId(shopId);
            cart.setNum(num);
            cart.setCreateTime(LocalDateTime.now());
            this.save(cart);
        }

        return Result.ok("已加入购物车", null);
    }

    @Override
    public Result listCart() {
        Long userId = UserHolder.getUser().getId();

        List<Cart> carts = this.list(
                new QueryWrapper<Cart>().eq("user_id", userId).orderByDesc("create_time")
        );

        if (carts.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }

        // 批量查询商品信息
        List<Long> shopIds = carts.stream().map(Cart::getShopId).collect(Collectors.toList());
        List<Shop> shops = shopMapper.selectBatchIds(shopIds);
        Map<Long, Shop> shopMap = shops.stream().collect(Collectors.toMap(Shop::getId, s -> s));

        // 组装 CartVO
        List<CartVO> voList = carts.stream().map(cart -> {
            CartVO vo = new CartVO();
            vo.setId(cart.getId());
            vo.setShopId(cart.getShopId());
            vo.setNum(cart.getNum());
            Shop shop = shopMap.get(cart.getShopId());
            if (shop != null) {
                vo.setProductName(shop.getProductName());
                vo.setDescription(shop.getDescription());
                vo.setPrice(shop.getPrice());
                vo.setImageUrl(shop.getImageUrl());
                vo.setStock(shop.getStock());
            }
            return vo;
        }).collect(Collectors.toList());

        return Result.ok(voList);
    }

    @Override
    public Result updateNum(Long id, Integer num) {
        Long userId = UserHolder.getUser().getId();
        Cart cart = this.getById(id);

        if (cart == null || !cart.getUserId().equals(userId)) {
            return Result.fail("购物车项不存在");
        }

        if (num <= 0) {
            this.removeById(id);
            return Result.ok("已移除", null);
        }

        cart.setNum(num);
        this.updateById(cart);
        return Result.ok("已更新", null);
    }

    @Override
    public Result removeFromCart(Long id) {
        Long userId = UserHolder.getUser().getId();
        Cart cart = this.getById(id);

        if (cart == null || !cart.getUserId().equals(userId)) {
            return Result.fail("购物车项不存在");
        }

        this.removeById(id);
        return Result.ok("已移除", null);
    }
}
