package org.example.onepiece.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("shop_type")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 图标路径
     */
    private String icon;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.DEFAULT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.DEFAULT)
    private LocalDateTime updateTime;
}