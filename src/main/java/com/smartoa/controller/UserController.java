package com.smartoa.controller;

import com.smartoa.dto.LoginDTO;
import com.smartoa.entity.User;
import com.smartoa.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestBody LoginDTO dto, HttpSession session) {
        User user = userService.login(dto.getUsername(), dto.getPassword());
        if (user == null) {
            return Map.of("success", false, "message", "用户名或密码错误");
        }
        session.setAttribute("userId", user.getId());
        String redirectUrl = "MANAGER".equals(user.getRole())
                ? "/manager/dashboard"
                : "/employee/dashboard";
        return Map.of("success", true, "redirectUrl", redirectUrl);
    }

    @GetMapping("/api/logout")
    public Map<String, Object> logout(HttpSession session) {
        session.invalidate();
        return Map.of("success", true);
    }
}
