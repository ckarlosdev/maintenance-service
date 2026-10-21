package com.hmbrandt.maintenance_service.controller.checklist;

import com.hmbrandt.maintenance_service.dto.checklist.ChecklistRequestDto;
import com.hmbrandt.maintenance_service.dto.checklist.ChecklistResponseDto;
import com.hmbrandt.maintenance_service.service.checklist.ChecklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/maintenance/checklist")
@RequiredArgsConstructor
@Tag(name = "Checklist control", description = "API to control the checklist capture by the end user")
public class ChecklistController {

    private final ChecklistService checklistService;

    @Operation(summary = "Save a checklist", description = "When a user save a checklist.")
    @PostMapping
    public ResponseEntity<ChecklistResponseDto> saveChecklist(
            @RequestBody ChecklistRequestDto checklistDto
            ){
        return new ResponseEntity<>(checklistService.saveChecklist(checklistDto), HttpStatus.CREATED);
    }

    @Operation(summary = "find a checklist", description = "Get a checklist by id.")
    @GetMapping("/{id}")
    public ResponseEntity<ChecklistResponseDto> getChecklistById(@PathVariable Long id){
        return ResponseEntity.ok(checklistService.findById(id));
    }

    @Operation(summary = "Checklists by vehicle id", description = "get a list of checklist by vehicle id")
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ChecklistResponseDto>> getChecklistsByVehicleID(
            @PathVariable Long vehicleId
    ){
        return ResponseEntity.ok(checklistService.getByVehicleId(vehicleId));
    }
}
