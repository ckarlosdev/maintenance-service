package com.hmbrandt.maintenance_service.dto.checklist;

import java.util.List;

public record TemplateResponseDto(
        Long templateId,
        String title,
        String description,
        List<ItemResponseDto> items
) {}
