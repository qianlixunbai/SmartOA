package com.smartoa.controller;

import com.smartoa.dto.LeaveSubmitDTO;
import com.smartoa.entity.ApprovalRecord;
import com.smartoa.entity.LeaveRequest;
import com.smartoa.entity.User;
import com.smartoa.service.LeaveService;
import com.smartoa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;
    private final UserService userService;

    @PostMapping("/api/leave/submit")
    public Map<String, Object> submitLeave(@RequestBody LeaveSubmitDTO dto) {
        User user = userService.getLoginUser();
        if (user == null) {
            return Map.of("success", false, "message", "请先登录");
        }
        if (user.getDirectLeaderId() == null) {
            return Map.of("success", false, "message", "您尚未分配直属领导，无法提交");
        }
        try {
            leaveService.submitLeave(user.getId(), dto);
            return Map.of("success", true, "message", "提交成功");
        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    @PostMapping("/api/leave/approve")
    public Map<String, Object> approveLeave(@RequestBody Map<String, String> body) {
        User user = userService.getLoginUser();
        if (user == null) {
            return Map.of("success", false, "message", "请先登录");
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

    @GetMapping("/api/leave/my-requests")
    public List<LeaveRequest> getMyRequests() {
        User user = userService.getLoginUser();
        return leaveService.getMyRequests(user.getId());
    }

    @GetMapping("/api/leave/pending")
    public List<LeaveRequest> getPendingRequests() {
        User user = userService.getLoginUser();
        return leaveService.getPendingRequests(user.getId());
    }

    @GetMapping("/api/leave/done")
    public List<LeaveRequest> getDoneRequests() {
        User user = userService.getLoginUser();
        return leaveService.getDoneRequests(user.getId());
    }

    @GetMapping("/api/leave/{id}")
    public LeaveRequest getRequestDetail(@PathVariable Long id) {
        return leaveService.getRequestDetail(id);
    }

    @GetMapping("/api/leave/{id}/records")
    public List<ApprovalRecord> getApprovalRecords(@PathVariable Long id) {
        return leaveService.getApprovalRecords(id);
    }
}
