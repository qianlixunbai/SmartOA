package com.smartoa.service;

import com.smartoa.entity.ApprovalTemplate;
import com.smartoa.repository.ApprovalTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final ApprovalTemplateRepository templateRepository;

    public List<ApprovalTemplate> listAll() {
        return templateRepository.findAll();
    }

    public ApprovalTemplate getById(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("模板不存在"));
    }

    @Transactional
    public ApprovalTemplate create(ApprovalTemplate template) {
        return templateRepository.save(template);
    }

    @Transactional
    public ApprovalTemplate update(Long id, ApprovalTemplate data) {
        ApprovalTemplate template = getById(id);
        template.setName(data.getName());
        template.setDescription(data.getDescription());
        template.setEnabled(data.isEnabled());
        return templateRepository.save(template);
    }

    @Transactional
    public void delete(Long id) {
        templateRepository.deleteById(id);
    }
}
