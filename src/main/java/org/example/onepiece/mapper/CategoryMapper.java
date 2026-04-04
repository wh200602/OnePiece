package org.example.onepiece.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.onepiece.entity.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMapper extends BaseMapper<Category> {
    // 继承 BaseMapper 后，可以直接使用 CRUD 操作，无需手动编写 SQL
}