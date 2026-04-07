package org.example.onepiece.service.Impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
//import cn.hutool.db.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.springframework.transaction.annotation.Transactional;

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

    //根据id查询商品详情
    @Override
    public Result queryById(Long id) {
        String key = "cache:shop:";
        //1、先到redis查询缓存
        String shopJson = stringRedisTemplate.opsForValue().get(key + id);
        //2、存在，直接返回
        if (StrUtil.isNotBlank(shopJson)) {
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }
        if (shopJson == null) {
            return Result.fail("店铺不存在");
        }
        //3、不存在，查询数据库
        Shop shop = getById(id);
        //4、查询不到，返回错误信息
        if (shop == null) {
            stringRedisTemplate.opsForValue().set(key + id, "", 2, TimeUnit.MINUTES);
            return Result.fail("店铺不存在");
        }
        //5、查询到，放入缓存
        stringRedisTemplate.opsForValue().set(key + id, JSONUtil.toJsonStr(shop), 30, TimeUnit.MINUTES);
        //6、返回数据
        return Result.ok(shop);
    }

    //分页查询商品列表（支持按分类筛选）
    @Override
    public Result queryByPage(int current, int size, String typeId) {
        String categoryPart = (typeId == null || typeId.isEmpty()) ? "all" : typeId;
        String key = "shop:page:" + current + ":" + size + ":type:" + categoryPart;

        try {
            // 1. 先从Redis查询
            String jsonStr = stringRedisTemplate.opsForValue().get(key);

            // 2. 判断缓存是否命中
            if (jsonStr != null && !jsonStr.isEmpty()) {
                PageInfo<Shop> cachedPageResult = JSONUtil.toBean(jsonStr, PageInfo.class);
                log.info("Cache Hit for key: {}", key);
                return Result.ok(cachedPageResult);
            }

            // 3. 缓存未命中，查询数据库
            log.info("Cache Miss for key: {}, querying database...", key);

            IPage<Shop> page = new Page<>(current, size);

            // 构建查询条件：按分类筛选
            QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
            if (typeId != null && !typeId.isEmpty()) {
                queryWrapper.eq("type_id", typeId);
            }

            IPage<Shop> dbPageResult = this.page(page, queryWrapper);

            // 4. 转换为 PageInfo
            PageInfo<Shop> pageInfo = new PageInfo<>();
            pageInfo.setRecords(dbPageResult.getRecords());
            pageInfo.setTotal(dbPageResult.getTotal());
            pageInfo.setCurrent(Math.toIntExact(dbPageResult.getCurrent()));
            pageInfo.setSize(Math.toIntExact(dbPageResult.getSize()));

            // 5. 存入Redis缓存，30分钟过期
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

    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();
        if (id == null || id <= 0) {
            return Result.fail("店铺ID不能为空");
        }
        updateById(shop);   // 更新数据库

        stringRedisTemplate.delete("cache:shop:" + id);  // 删除缓存
        return Result.ok("更新成功");
    }
}
