package com.smartoa.controller;

import com.smartoa.entity.User;
import com.smartoa.service.LeaveService;
import com.smartoa.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final UserService userService;
    private final LeaveService leaveService;

    @GetMapping("/")
    public String index(HttpSession session) {
        User user = userService.getLoginUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        return "MANAGER".equals(user.getRole())
                ? "redirect:/manager/dashboard"
                : "redirect:/employee/dashboard";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/employee/dashboard")
    public String employeeDashboard(HttpSession session, Model model) {
        User user = userService.getLoginUser(session);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("requests", leaveService.getMyRequests(user.getId()));
        return "employee/dashboard";
    }

    @GetMapping("/employee/detail/{id}")
    public String employeeDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = userService.getLoginUser(session);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("request", leaveService.getRequestDetail(id));
        model.addAttribute("records", leaveService.getApprovalRecords(id));
        return "employee/detail";
    }

    @GetMapping("/manager/dashboard")
    public String managerDashboard(HttpSession session, Model model) {
        User user = userService.getLoginUser(session);
        if (user == null) return "redirect:/login";
        if (!"MANAGER".equals(user.getRole())) return "redirect:/employee/dashboard";
        model.addAttribute("user", user);
        model.addAttribute("requests", leaveService.getPendingRequests());
        return "manager/dashboard";
    }

    @GetMapping("/manager/approve/{id}")
    public String approvePage(@PathVariable Long id, HttpSession session, Model model) {
        User user = userService.getLoginUser(session);
        if (user == null) return "redirect:/login";
        if (!"MANAGER".equals(user.getRole())) return "redirect:/employee/dashboard";
        model.addAttribute("user", user);
        model.addAttribute("request", leaveService.getRequestDetail(id));
        model.addAttribute("records", leaveService.getApprovalRecords(id));
        return "manager/approve";
    }
}
