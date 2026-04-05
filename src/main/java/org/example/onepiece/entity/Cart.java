package org.example.onepiece.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 购物车实体类
 */
@Data
@TableName("cart") // 对应数据库表名
public class Cart {

    // 主键 自增
    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户ID
    private Long userId;

    // 商品ID
    private Long shopId;

    // 商品数量
    private Integer num;

    // 创建时间
    private LocalDateTime createTime;
}