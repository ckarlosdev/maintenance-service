package com.hmbrandt.maintenance_service.service.checklist;

import com.hmbrandt.maintenance_service.dto.checklist.ChecklistRequestDto;
import com.hmbrandt.maintenance_service.dto.checklist.ChecklistResponseDto;

import java.util.List;

public interface ChecklistService {
    ChecklistResponseDto saveChecklist(ChecklistRequestDto checklistDto);
    ChecklistResponseDto findById(Long id);
    List<ChecklistResponseDto> getByVehicleId(Long vehicleId);

}
