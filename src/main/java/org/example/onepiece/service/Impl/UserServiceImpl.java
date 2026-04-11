package org.example.onepiece.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.example.onepiece.dto.LoginFormDTO;
import org.example.onepiece.dto.UserDTO;
import org.example.onepiece.entity.User;
import org.example.onepiece.mapper.UserMapper;
import org.example.onepiece.service.IUserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.example.onepiece.utils.RegexUtils;
import org.example.onepiece.dto.Result;

import java.util.HashMap;
import java.util.Map;

import static org.example.onepiece.utils.RedisConstants.LOGIN_CODE_KEY;
import static org.example.onepiece.utils.RedisConstants.LOGIN_USER_KEY;


@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result sendCode(String phone, HttpSession session) {
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误");
        }
        String code = RandomUtil.randomNumbers(6);
//        session.setAttribute("code", code);
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY+phone, code, 5, java.util.concurrent.TimeUnit.MINUTES);
        log.info("验证码为：{}", code);
//        return Result.ok();
        return Result.ok("验证码发送成功", null);
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        String phone = loginForm.getPhone();

//        Object code = session.getAttribute("code");

        String code = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        if(code == null || !loginForm.getCode().equals(code)){
            return Result.fail("验证码错误");
        }

        User user = query().eq("phone", phone).one();
        if(user == null){
            user = creatUserWithPhone(phone);
        }
//        session.setAttribute("user", user);
        String token = UUID.randomUUID().toString(true);
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)  // 忽略空值
                        .setFieldValueEditor((fieldName, fieldValue) ->
                                fieldValue != null ? fieldValue.toString() : ""
                        ));

//        log.info("生成的 token: {}", token);
//        log.info("userMap: {}", userMap);
//        log.info("要写入的 Redis key: {}", LOGIN_USER_KEY + token);

        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, 36000, java.util.concurrent.TimeUnit.SECONDS);
//        return Result.ok();
        return Result.ok("登录成功", token);
    }

    private User creatUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setUsername("to_user"+RandomUtil.randomString(10));
        save(user);
//        baseMapper.insert(user);
        return user;
    }

}
