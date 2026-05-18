package com.smartoa.controller;

import com.smartoa.dto.LeaveSubmitDTO;
import com.smartoa.entity.User;
import com.smartoa.service.LeaveService;
import com.smartoa.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;
    private final UserService userService;

    @PostMapping("/api/leave/submit")
    public Map<String, Object> submitLeave(@RequestBody LeaveSubmitDTO dto, HttpSession session) {
        User user = userService.getLoginUser(session);
        if (user == null) {
            return Map.of("success", false, "message", "请先登录");
        }
        try {
            leaveService.submitLeave(user.getId(), dto);
            return Map.of("success", true, "message", "提交成功");
        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    @PostMapping("/api/leave/approve")
    public Map<String, Object> approveLeave(@RequestBody Map<String, String> body, HttpSession session) {
        User user = userService.getLoginUser(session);
        if (user == null) {
            return Map.of("success", false, "message", "请先登录");
        }
        if (!"MANAGER".equals(user.getRole())) {
            return Map.of("success", false, "message", "无权限");
        }
        try {
            Long requestId = Long.valueOf(body.get("requestId"));
            String action = body.get("action");
            String comment = body.getOrDefault("comment", "");
            leaveService.approveLeave(requestId, user.getId(), action, comment);
            return Map.of("success", true, "message", "操作成功");
        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }
}
