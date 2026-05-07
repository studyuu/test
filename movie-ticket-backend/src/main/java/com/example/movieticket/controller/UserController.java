package com.example.movieticket.controller;

import com.example.movieticket.entity.SysUser;
import com.example.movieticket.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private SysUserRepository userRepository;

    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<SysUser> user = userRepository.findById(id);
            if (user.isPresent()) {
                response.put("code", 200);
                response.put("message", "success");
                response.put("data", toResponseMap(user.get()));
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

    @GetMapping
    public Map<String, Object> getAllUsers() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SysUser> users = userRepository.findAllByIsDeleted(0);
            List<Map<String, Object>> userList = users.stream()
                    .map(this::toResponseMap)
                    .collect(Collectors.toList());

            response.put("code", 200);
            response.put("message", "success");
            response.put("data", userList);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取用户列表失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    @PostMapping
    public Map<String, Object> addUser(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = (String) request.get("username");
            String password = (String) request.get("password");
            String nickname = (String) request.get("nickname");

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
            user.setEmail((String) request.get("email"));
            user.setPhone((String) request.get("phone"));
            user.setAvatar((String) request.get("avatar"));
            user.setRole(request.containsKey("role") ? (String) request.get("role") : "user");
            user.setStatus(1);
            user.setIsDeleted(0);

            SysUser savedUser = userRepository.save(user);

            response.put("code", 200);
            response.put("message", "添加成功");
            response.put("data", toResponseMap(savedUser));
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "添加失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<SysUser> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                SysUser user = optionalUser.get();

                if (request.containsKey("username")) {
                    user.setUsername((String) request.get("username"));
                }
                if (request.containsKey("nickname")) {
                    user.setNickname((String) request.get("nickname"));
                }
                if (request.containsKey("email")) {
                    user.setEmail((String) request.get("email"));
                }
                if (request.containsKey("phone")) {
                    user.setPhone((String) request.get("phone"));
                }
                if (request.containsKey("avatar")) {
                    user.setAvatar((String) request.get("avatar"));
                }
                if (request.containsKey("role")) {
                    user.setRole((String) request.get("role"));
                }

                user.setUpdateTime(LocalDateTime.now());
                SysUser updatedUser = userRepository.save(user);

                response.put("code", 200);
                response.put("message", "更新成功");
                response.put("data", toResponseMap(updatedUser));
            } else {
                response.put("code", 404);
                response.put("message", "用户不存在");
                response.put("data", null);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    @PutMapping("/{id}/status")
    public Map<String, Object> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<SysUser> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                SysUser user = optionalUser.get();
                String status = (String) request.get("status");

                if ("active".equals(status)) {
                    user.setStatus(1);
                } else if ("disabled".equals(status)) {
                    user.setStatus(0);
                } else {
                    response.put("code", 400);
                    response.put("message", "无效的状态值");
                    response.put("data", null);
                    return response;
                }

                user.setUpdateTime(LocalDateTime.now());
                SysUser updatedUser = userRepository.save(user);

                response.put("code", 200);
                response.put("message", "状态更新成功");
                response.put("data", toResponseMap(updatedUser));
            } else {
                response.put("code", 404);
                response.put("message", "用户不存在");
                response.put("data", null);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<SysUser> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                SysUser user = optionalUser.get();
                user.setIsDeleted(1);
                user.setUpdateTime(LocalDateTime.now());
                userRepository.save(user);

                response.put("code", 200);
                response.put("message", "删除成功");
                response.put("data", null);
            } else {
                response.put("code", 404);
                response.put("message", "用户不存在");
                response.put("data", null);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    private Map<String, Object> toResponseMap(SysUser user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getUserId());
        map.put("userId", user.getUserId());
        map.put("username", user.getUsername());
        map.put("nickname", user.getNickname());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("avatar", user.getAvatar());
        map.put("role", user.getRole() != null ? user.getRole() : "user");
        map.put("status", user.getStatus() == 1 ? "active" : "disabled");

        if (user.getCreateTime() != null) {
            String createTime = user.getCreateTime().toString().replace("T", " ");
            if (createTime.length() > 19) {
                createTime = createTime.substring(0, 19);
            }
            map.put("createTime", createTime);
        }

        if (user.getUpdateTime() != null) {
            String updateTime = user.getUpdateTime().toString().replace("T", " ");
            if (updateTime.length() > 19) {
                updateTime = updateTime.substring(0, 19);
            }
            map.put("updateTime", updateTime);
        }

        return map;
    }
}
