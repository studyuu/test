package com.example.movieticket.controller;

import com.example.movieticket.entity.SysUser;
import com.example.movieticket.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
                if (user.getStatus() == 0) {
                    response.put("code", 401);
                    response.put("message", "用户已被禁用");
                    response.put("data", null);
                    return response;
                }
                if (password.equals(user.getPassword())) {
                    long expireTime = System.currentTimeMillis() + 8 * 60 * 60 * 1000;
                    String token = "MT_" + System.currentTimeMillis() + "_" + user.getUserId();

                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", user.getUserId());
                    userInfo.put("userId", user.getUserId());
                    userInfo.put("username", user.getUsername());
                    userInfo.put("nickname", user.getNickname());
                    userInfo.put("email", user.getEmail());
                    userInfo.put("phone", user.getPhone());
                    userInfo.put("avatar", user.getAvatar());
                    userInfo.put("role", user.getRole() != null ? user.getRole() : "user");
                    userInfo.put("token", token);
                    userInfo.put("expireTime", expireTime);

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

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String username = request.get("username");
        String password = request.get("password");
        String nickname = request.get("nickname");
        String phone = request.get("phone");

        try {
            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                response.put("code", 400);
                response.put("message", "用户名和密码不能为空");
                response.put("data", null);
                return response;
            }

            if (userRepository.findByUsername(username).isPresent()) {
                response.put("code", 409);
                response.put("message", "用户名已存在");
                response.put("data", null);
                return response;
            }

            SysUser user = new SysUser();
            user.setUsername(username);
            user.setPassword(password);
            user.setNickname(nickname != null ? nickname : username);
            user.setPhone(phone);
            user.setRole("user");
            user.setStatus(1);
            user.setIsDeleted(0);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());

            SysUser savedUser = userRepository.save(user);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", savedUser.getUserId());
            userInfo.put("userId", savedUser.getUserId());
            userInfo.put("username", savedUser.getUsername());
            userInfo.put("nickname", savedUser.getNickname());
            userInfo.put("email", savedUser.getEmail());
            userInfo.put("phone", savedUser.getPhone());
            userInfo.put("avatar", savedUser.getAvatar());
            userInfo.put("role", "user");

            response.put("code", 200);
            response.put("message", "注册成功");
            response.put("data", userInfo);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "注册失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("code", 200);
            response.put("message", "退出成功");
            response.put("data", null);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "退出失败: " + e.getMessage());
            response.put("data", null);
        }
        return response;
    }

    @GetMapping("/current-user")
    public Map<String, Object> getCurrentUser(@RequestParam(required = false) String username) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (username == null || username.isEmpty()) {
                response.put("code", 400);
                response.put("message", "用户名不能为空");
                response.put("data", null);
                return response;
            }

            Optional<SysUser> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                SysUser user = userOptional.get();
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getUserId());
                userInfo.put("userId", user.getUserId());
                userInfo.put("username", user.getUsername());
                userInfo.put("nickname", user.getNickname());
                userInfo.put("email", user.getEmail());
                userInfo.put("phone", user.getPhone());
                userInfo.put("avatar", user.getAvatar());
                userInfo.put("role", user.getRole() != null ? user.getRole() : "user");

                response.put("code", 200);
                response.put("message", "success");
                response.put("data", userInfo);
            } else {
                response.put("code", 404);
                response.put("message", "用户不存在");
                response.put("data", null);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取用户信息失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }
}