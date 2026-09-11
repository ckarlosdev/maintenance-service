package com.hmbrandt.maintenance_service.dto;

import java.time.LocalDateTime;

public record EquipmentIssueRequestDto(
        Long equipmentId,
        String reportedBy,
        String issueDescription,
        String severity,
        String userName,
        Long referenceID,
        String issueType,
        String details
) {}
