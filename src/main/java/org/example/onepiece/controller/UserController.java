package org.example.onepiece.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.example.onepiece.dto.LoginFormDTO;
import org.example.onepiece.dto.Result;
import org.example.onepiece.service.IUserService;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private IUserService userService;

    //注册
    @PostMapping("/code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        //发送短信验证码并保存验证码
        return userService.sendCode(phone, session);
    }
    //登录
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session){
        //实现登录功能
        return userService.login(loginForm,session);
    }
}
