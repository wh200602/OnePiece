package org.example.onepiece.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.onepiece.entity.User;
//package org.example.onepiece.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper; // 关键注解
import org.example.onepiece.entity.User;

public interface UserMapper extends BaseMapper<User> {

}
