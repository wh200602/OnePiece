package org.example.onepiece.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_seckill_voucher")
public class Voucher {

    // 主键 ID，数据库自增，插入时忽略
    @TableId(type = IdType.AUTO)
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long id;

    // 商铺ID
    private Long shopId;

    // 优惠券标题
    private String title;

    // 副标题
    private String subTitle;

    // 使用规则
    private String rules;

    // 支付金额（单位：分）
    private Long payValue;

    // 抵扣金额（单位：分）
    private Long actualValue;

    // 状态 1：上架 2：下架 3：过期
    private Integer status;

    // 生效时间
    private LocalDateTime beginTime;

    // 失效时间
    private LocalDateTime endTime;

    // 库存
    private Integer stock;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}