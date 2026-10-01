package com.hmbrandt.maintenance_service.dto.Notification;


import com.hmbrandt.maintenance_service.dto.EquipmentIssueRequestDto;

public record EquipmentIssueCreateDto(
        EquipmentIssueRequestDto issueData,
        EquipmentDataDto equipmentData
) {}
