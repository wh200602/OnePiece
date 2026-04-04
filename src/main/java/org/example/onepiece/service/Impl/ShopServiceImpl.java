package org.example.onepiece.service.Impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
//import cn.hutool.db.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.onepiece.dto.Result;
import org.example.onepiece.entity.Category;
import org.example.onepiece.entity.Shop;
import org.example.onepiece.mapper.CategoryMapper;
import org.example.onepiece.mapper.ShopMapper;
import org.example.onepiece.service.IShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import cn.hutool.json.JSONUtil;
import org.example.onepiece.utils.PageInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public Result queryById(Long id) {
        String key = "cache:shop:";
        //1、先到redis查询缓存
        String shopJson = stringRedisTemplate.opsForValue().get(key + id);
        //2、存在，直接返回
        if(StrUtil.isNotBlank(shopJson)){
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }
        //3、不存在，查询数据库
        Shop shop = getById(id);
        //4、查询不到，返回错误信息
        if(shop == null){
            return Result.fail("店铺不存在");
        }
        //5、查询到，放入缓存
        stringRedisTemplate.opsForValue().set(key + id, JSONUtil.toJsonStr(shop));
        //6、返回数据
        return Result.ok(shop);
    }

    @Override
    public Result queryByPage(int current, int size) {
        String key = "shop:page:" + current + ":" + size;

        try {
            // 1. 先从Redis查询
            String jsonStr = stringRedisTemplate.opsForValue().get(key);

            // 2. 判断缓存是否命中
            if (jsonStr != null && !jsonStr.isEmpty()) {
                // 缓存命中，反序列化为 PageInfo<Shop>
                PageInfo<Shop> cachedPageResult = JSONUtil.toBean(jsonStr, PageInfo.class);
                log.info("Cache Hit for key: {}", key);
                return Result.ok(cachedPageResult);
            }

            // 3. 缓存未命中，查询数据库
            log.info("Cache Miss for key: {}, querying database...", key);

            // 创建分页对象，使用接口类型
            IPage<Shop> page = new Page<>(current, size);

            // 使用MyBatis-Plus的内置分页查询方法，返回类型也是 IPage
            IPage<Shop> dbPageResult = this.page(page);

            // 4. 将 MyBatis-Plus 的 Page 结果转换为我们自定义的 PageInfo 对象
            PageInfo<Shop> pageInfo = new PageInfo<>();
            pageInfo.setRecords(dbPageResult.getRecords()); // ✅ 通过 IPage 接口访问方法
            pageInfo.setTotal(dbPageResult.getTotal());
            pageInfo.setCurrent(Math.toIntExact(dbPageResult.getCurrent()));
            pageInfo.setSize(Math.toIntExact(dbPageResult.getSize()));

            // 5. 将 PageInfo 对象存入Redis缓存
            String jsonString = JSONUtil.toJsonStr(pageInfo);
            stringRedisTemplate.opsForValue().set(key, jsonString, 30, TimeUnit.MINUTES);

            log.info("Data loaded from DB and cached for key: {}", key);
            return Result.ok(pageInfo);

        } catch (Exception e) {
            log.error("Error querying products with pagination from cache or DB", e);
            return Result.fail("查询商品列表失败");
        }
    }

    @Override
    public Result queryCategories() {
        List<Category> categories = categoryMapper.selectList(null);

        // 将结果封装到 Result 对象中返回
        return Result.ok(categories);
    }
}
