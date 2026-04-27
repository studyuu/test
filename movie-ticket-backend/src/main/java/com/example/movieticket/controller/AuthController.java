package com.example.movieticket.controller;

import com.example.movieticket.entity.SysUser;
import com.example.movieticket.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserRepository userRepository;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String username = request.get("username");
        String password = request.get("password");

        try {
            Optional<SysUser> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                SysUser user = userOptional.get();
                if (password.equals(user.getPassword())) {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", user.getUserId());
                    userInfo.put("userId", user.getUserId());
                    userInfo.put("username", user.getUsername());
                    userInfo.put("nickname", user.getNickname());
                    userInfo.put("email", user.getEmail());
                    userInfo.put("phone", user.getPhone());
                    userInfo.put("avatar", user.getAvatar());
                    userInfo.put("role", user.getUsername().equals("admin") ? "admin" : "user");

                    response.put("code", 200);
                    response.put("message", "登录成功");
                    response.put("data", userInfo);
                } else {
                    response.put("code", 401);
                    response.put("message", "密码错误");
                    response.put("data", null);
                }
            } else {
                response.put("code", 401);
                response.put("message", "用户不存在");
                response.put("data", null);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "登录失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }
}