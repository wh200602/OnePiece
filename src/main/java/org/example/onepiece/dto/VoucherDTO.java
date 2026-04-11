package org.example.onepiece.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 新增优惠券请求体
 */
@Data
public class VoucherDTO {
    private Long shopId;          // 所属商铺 ID
    private String title;         // 优惠券标题
    private String subTitle;      // 副标题
    private String rules;         // 使用规则
    private Long payValue;        // 支付金额（分）
    private Long actualValue;     // 抵扣金额（分）
    private Integer status;       // 1=上架 2=下架 3=过期
    private LocalDateTime beginTime; // 生效时间
    private LocalDateTime endTime;   // 失效时间
    private Integer stock;           // 库存
}
