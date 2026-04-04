package org.example.onepiece.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpSession;
import org.example.onepiece.dto.LoginFormDTO;
import org.example.onepiece.dto.Result;
import org.example.onepiece.entity.User;

public interface IUserService extends IService<User> {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);

//    Result login(LoginFormDTO loginForm, HttpSession session);

}