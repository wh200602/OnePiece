package org.example.onepiece.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Shop {
    @TableId(type = IdType.AUTO)
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long id;

    private String productName;
    private String type_id;
    private String description;
    private Double price;
    private Integer stock;
    private Boolean isLimited;
    private String imageUrl;
    private String releaseDate;
    private String createdAt;
    private String updatedAt;

}
