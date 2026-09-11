package com.hmbrandt.maintenance_service.dto;

import java.time.LocalDateTime;

public record EquipmentIssueResponseDto(
        Long id,
        Long equipmentId,
        Long referenceId,
        String reportedBy,
        LocalDateTime reportedAt,
        String issueType,
        String issueDescription,
        String details,
        String severity,
        String issueStatus,
        Long workOrderId,
        Long parentIssueId
) {}
