package org.example.onepiece.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


/**
 * 用户实体类（对应数据库 user 表）
 * 用于封装数据库中用户的所有原始数据
 */
@Data // Lombok 注解，自动生成 getter/setter/toString/equals/hashCode 等方法
public class User {
    /**
     * 主键ID（数据库自增）
     */
    @TableId(type = IdType.AUTO)
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long id;

    /**
     * 用户名（登录账号）
     */
    private String username;

    /**
     * 密码（加密存储）
     */

    private String phone;


}