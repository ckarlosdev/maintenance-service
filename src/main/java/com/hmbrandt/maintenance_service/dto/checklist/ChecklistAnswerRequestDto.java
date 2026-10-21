package com.hmbrandt.maintenance_service.dto.checklist;


public record ChecklistAnswerRequestDto(
        Long id,
        Long itemId,
        String answer,
        String comment
) {}
