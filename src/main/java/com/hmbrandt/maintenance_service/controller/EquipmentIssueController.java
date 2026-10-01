package com.hmbrandt.maintenance_service.controller;

import com.hmbrandt.maintenance_service.dto.*;
import com.hmbrandt.maintenance_service.dto.Notification.EquipmentIssueCreateDto;
import com.hmbrandt.maintenance_service.service.EquipmentIssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/maintenance/issue")
@RequiredArgsConstructor
@Tag(name = "Equipment issues", description = "Service to control equipment issues")
public class EquipmentIssueController {

    private final EquipmentIssueService equipmentIssueService;

    @PostMapping
    public ResponseEntity<EquipmentIssueResponseDto> createIssue(
            @Valid @RequestBody EquipmentIssueCreateDto issueDto
    ){
        return new ResponseEntity<>(equipmentIssueService.saveIssue(issueDto.issueData(), issueDto.equipmentData()), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EquipmentIssueResponseDto> updateIssue(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentIssueUpdateDto issueDto
    ){
        EquipmentIssueResponseDto updatedIssue = equipmentIssueService.updateIssue(id, issueDto);
        return ResponseEntity.ok(updatedIssue);
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<EquipmentIssueResponseDto> statusIssueChange(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentIssueStatusDto issueDto
    ){
        EquipmentIssueResponseDto updatedIssue = equipmentIssueService.updateStatus(id, issueDto);
        return ResponseEntity.ok(updatedIssue);
    }

    @PatchMapping("/parent/{id}")
    public ResponseEntity<EquipmentIssueResponseDto> parentIssue(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentIssueParentDto issueDto
    ){
        EquipmentIssueResponseDto updatedIssue = equipmentIssueService.parentIssue(id, issueDto);
        return ResponseEntity.ok(updatedIssue);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        equipmentIssueService.deleteIssue(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<EquipmentIssueResponseDto>> getIssuesByEquipmentId(
            @PathVariable Long equipmentId
    ){
        return ResponseEntity.ok(
                equipmentIssueService.findByEquipmentId(equipmentId));
    }

    @Operation(summary = "Get Active Reports", description = "Get the reports actives without work order")
    @GetMapping("/reports")
    public ResponseEntity<List<EquipmentIssueResponseDto>> getAllReports(){
        List<EquipmentIssueResponseDto> reports = equipmentIssueService.getReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentIssueResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentIssueService.findById(id));
    }

    @PostMapping("/active-by-equipments")
    public ResponseEntity<Map<Long, List<EquipmentIssueSummaryDto>>> getActiveIssuesByEquipmentIds(
            @RequestBody List<Long> equipmentIds
    ) {
        Map<Long, List<EquipmentIssueSummaryDto>> response = equipmentIssueService.getActiveIssuesByEquipmentIds(equipmentIds);
        return ResponseEntity.ok(response);
    }

}
