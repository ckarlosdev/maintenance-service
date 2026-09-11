package com.hmbrandt.maintenance_service.dto;

public record EquipmentIssueUpdateDto(
        Long equipmentId,
        String reportedBy,
        String issueType,
        String issueDescription,
        String details,
        String severity,
        String userName
) {
}
