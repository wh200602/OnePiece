package org.example.onepiece.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.onepiece.dto.Result;
import org.example.onepiece.entity.Shop;

public interface IShopService extends IService<Shop> {
    Result queryById(Long id);

    Result queryByPage(int current, int size, String typeId);

    Result queryCategories();

    Result update(Shop shop);
}
