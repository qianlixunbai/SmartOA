package com.smartoa.controller;

import com.smartoa.entity.ApprovalNode;
import com.smartoa.entity.ApprovalTemplate;
import com.smartoa.entity.TemplateField;
import com.smartoa.entity.User;
import com.smartoa.service.TemplateService;
import com.smartoa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;
    private final UserService userService;

    @GetMapping("/api/templates")
    public List<ApprovalTemplate> list() {
        return templateService.listAll();
    }

    @GetMapping("/api/templates/{id}")
    public ApprovalTemplate getById(@PathVariable Long id) {
        return templateService.getById(id);
    }

    @PostMapping("/api/templates")
    public Map<String, Object> create(@RequestBody ApprovalTemplate template) {
        User user = userService.getLoginUser();
        if (!"MANAGER".equals(user.getRole())) {
            return Map.of("success", false, "message", "无权限");
        }
        templateService.create(template);
        return Map.of("success", true, "message", "创建成功");
    }

    @PutMapping("/api/templates/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody ApprovalTemplate data) {
        User user = userService.getLoginUser();
        if (!"MANAGER".equals(user.getRole())) {
            return Map.of("success", false, "message", "无权限");
        }
        templateService.update(id, data);
        return Map.of("success", true, "message", "更新成功");
    }

    @DeleteMapping("/api/templates/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        User user = userService.getLoginUser();
        if (!"MANAGER".equals(user.getRole())) {
            return Map.of("success", false, "message", "无权限");
        }
        templateService.delete(id);
        return Map.of("success", true, "message", "删除成功");
    }

    // ========== 审批节点 ==========

    @GetMapping("/api/templates/{id}/nodes")
    public List<ApprovalNode> listNodes(@PathVariable Long id) {
        return templateService.listNodes(id);
    }

    @PostMapping("/api/templates/{id}/nodes")
    public Map<String, Object> saveNodes(@PathVariable Long id, @RequestBody List<ApprovalNode> nodes) {
        User user = userService.getLoginUser();
        if (!"MANAGER".equals(user.getRole())) {
            return Map.of("success", false, "message", "无权限");
        }
        templateService.saveNodes(id, nodes);
        return Map.of("success", true, "message", "保存成功");
    }

    @DeleteMapping("/api/templates/{id}/nodes/{nodeId}")
    public Map<String, Object> deleteNode(@PathVariable Long id, @PathVariable Long nodeId) {
        User user = userService.getLoginUser();
        if (!"MANAGER".equals(user.getRole())) {
            return Map.of("success", false, "message", "无权限");
        }
        templateService.deleteNode(nodeId);
        return Map.of("success", true, "message", "删除成功");
    }

    // ========== 表单字段 ==========

    @GetMapping("/api/templates/{id}/fields")
    public List<TemplateField> listFields(@PathVariable Long id) {
        return templateService.listFields(id);
    }
}
