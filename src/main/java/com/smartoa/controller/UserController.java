package com.smartoa.controller;

import com.smartoa.config.JwtUtil;
import com.smartoa.dto.LoginDTO;
import com.smartoa.entity.User;
import com.smartoa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestBody LoginDTO dto) {
        User user = userService.login(dto.getUsername(), dto.getPassword());
        if (user == null) {
            return Map.of("success", false, "message", "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("realName", user.getRealName());
        userMap.put("role", user.getRole());
        userMap.put("department", user.getDepartment());
        userMap.put("directLeaderId", user.getDirectLeaderId());
        userMap.put("departmentHeadId", user.getDepartmentHeadId());
        return Map.of("success", true, "token", token, "user", userMap);
    }

    @GetMapping("/api/user/current")
    public Map<String, Object> currentUser() {
        User user = userService.getLoginUser();
        if (user == null) {
            return Map.of("success", false, "message", "未登录");
        }
        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("realName", user.getRealName());
        userMap.put("role", user.getRole());
        userMap.put("department", user.getDepartment());
        userMap.put("directLeaderId", user.getDirectLeaderId());
        userMap.put("departmentHeadId", user.getDepartmentHeadId());
        return Map.of("success", true, "user", userMap);
    }

    @PostMapping("/api/logout")
    public Map<String, Object> logout() {
        return Map.of("success", true);
    }
}
