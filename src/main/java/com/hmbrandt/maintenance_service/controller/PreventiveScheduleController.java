package com.hmbrandt.maintenance_service.controller;


import com.hmbrandt.maintenance_service.dto.PreventiveScheduleRequestDto;
import com.hmbrandt.maintenance_service.dto.PreventiveScheduleResponseDto;
import com.hmbrandt.maintenance_service.dto.PreventiveScheduleResponseProjection;
import com.hmbrandt.maintenance_service.service.PreventiveScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/maintenance/schedule")
@RequiredArgsConstructor
@Tag(name = "Preventive schedule", description = "ServiceRepository to control the preventive schedule data")
public class PreventiveScheduleController {

    private final PreventiveScheduleService preventiveScheduleService;

    @PostMapping
    public ResponseEntity<PreventiveScheduleResponseDto> createSchedule(
            @Valid @RequestBody PreventiveScheduleRequestDto scheduleDto
            ){
        return new ResponseEntity<>(preventiveScheduleService.save(scheduleDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PreventiveScheduleResponseDto> updateIssue(
            @PathVariable Long id,
            @Valid @RequestBody PreventiveScheduleRequestDto scheduleDto
    ){
        PreventiveScheduleResponseDto updatedSchedule = preventiveScheduleService.update(id, scheduleDto);
        return ResponseEntity.ok(updatedSchedule);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteById(
            @PathVariable Long scheduleId
    ){
        preventiveScheduleService.delete(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<PreventiveScheduleResponseDto>> getSchedulesByEquipmentId(
            @PathVariable Long equipmentId
    ){
        return ResponseEntity.ok(preventiveScheduleService.findByEquipmentId(equipmentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreventiveScheduleResponseDto> getSchedulesById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(preventiveScheduleService.findById(id));
    }


    @PostMapping("/active-by-equipments")
    public ResponseEntity<List<PreventiveScheduleResponseProjection>> getActiveSchedulesByEquipmentIds(
            @RequestBody List<Long> equipmentIds
    ) {
        List<PreventiveScheduleResponseProjection> result = preventiveScheduleService.getActiveSchedulesForEquipments(equipmentIds);
        return ResponseEntity.ok(result);
    }

}
