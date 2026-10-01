package com.hmbrandt.maintenance_service.service;


import com.hmbrandt.maintenance_service.dto.*;
import com.hmbrandt.maintenance_service.dto.Notification.EquipmentDataDto;

import java.util.List;
import java.util.Map;

public interface EquipmentIssueService {
    EquipmentIssueResponseDto saveIssue(EquipmentIssueRequestDto dto, EquipmentDataDto equipData);

    EquipmentIssueResponseDto updateIssue(Long id, EquipmentIssueUpdateDto dto);

    EquipmentIssueResponseDto findById(Long id);

    List<EquipmentIssueResponseDto> getReports();

    List<EquipmentIssueResponseDto> findByEquipmentId(Long JobId);

    void deleteIssue(Long id);

    EquipmentIssueResponseDto updateStatus(Long id, EquipmentIssueStatusDto dto);

    EquipmentIssueResponseDto parentIssue(Long id, EquipmentIssueParentDto dto);

    Map<Long, List<EquipmentIssueSummaryDto>> getActiveIssuesByEquipmentIds(List<Long> equipmentIds);
}
