package com.hmbrandt.maintenance_service.dto.checklist;

import com.hmbrandt.maintenance_service.entity.checklist.ChecklistTemplate;

import java.time.LocalDateTime;
import java.util.List;

public record ChecklistResponseDto(
        Long id,
        Long templateId,
        Long vehicleId,
        String checklistStatus,
        String generalComment,
        List<ChecklistAnswerResponseDto> answers
) {}
