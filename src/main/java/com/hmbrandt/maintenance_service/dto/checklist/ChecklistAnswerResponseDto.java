package com.hmbrandt.maintenance_service.dto.checklist;

public record ChecklistAnswerResponseDto(
        Long id,
        Long checklistId,
        Long checklistItemId,
        String answer,
        String answerComment
) {}
