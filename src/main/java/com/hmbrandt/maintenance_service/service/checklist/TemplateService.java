package com.hmbrandt.maintenance_service.service.checklist;


import com.hmbrandt.maintenance_service.dto.checklist.TemplateResponseDto;

public interface TemplateService {
    TemplateResponseDto getTemplateById(Long templateId);
}
