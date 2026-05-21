package com.smartoa.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartoa.entity.ApprovalTemplate;
import com.smartoa.mapper.ApprovalTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final ApprovalTemplateMapper templateMapper;

    public List<ApprovalTemplate> listAll() {
        return templateMapper.selectList(null);
    }

    public ApprovalTemplate getById(Long id) {
        return templateMapper.selectById(id);
    }

    @Transactional
    public void create(ApprovalTemplate template) {
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());
        templateMapper.insert(template);
    }

    @Transactional
    public void update(Long id, ApprovalTemplate data) {
        ApprovalTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        template.setName(data.getName());
        template.setDescription(data.getDescription());
        template.setEnabled(data.isEnabled());
        template.setUpdateTime(LocalDateTime.now());
        templateMapper.updateById(template);
    }

    @Transactional
    public void delete(Long id) {
        templateMapper.deleteById(id);
    }
}
