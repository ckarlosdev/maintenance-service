package com.hmbrandt.maintenance_service.service;

import com.hmbrandt.maintenance_service.dto.*;
import com.hmbrandt.maintenance_service.entity.EquipmentIssue;
import com.hmbrandt.maintenance_service.entity.WorkOrder;
import com.hmbrandt.maintenance_service.repository.EquipmentIssueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentIssueServiceImpl implements EquipmentIssueService {

    private final EquipmentIssueRepository equipmentIssueRepository;

    @Override
    @Transactional
    public EquipmentIssueResponseDto saveIssue(EquipmentIssueRequestDto dto){

        EquipmentIssue newIssue = new EquipmentIssue();
        newIssue.setEquipmentId(dto.equipmentId());
        newIssue.setReferenceId(dto.referenceID());
        newIssue.setReportedBy(dto.reportedBy());
        newIssue.setReportedAt(LocalDateTime.now());
        newIssue.setIssueType(dto.issueType());
        newIssue.setIssueDescription(dto.issueDescription());
        newIssue.setDetails(dto.details());
        newIssue.setSeverity(dto.severity());
        newIssue.setIssueStatus("OPEN");
        newIssue.setCreatedBy(dto.userName());
        newIssue.setUpdatedBy(dto.userName());

        EquipmentIssue savedIssue = equipmentIssueRepository.save(newIssue);

        return mapIssueToDto(savedIssue);
    }

    @Override
    @Transactional
    public EquipmentIssueResponseDto updateIssue(Long id, EquipmentIssueUpdateDto dto){
        EquipmentIssue issue = equipmentIssueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id: "+id));

        issue.setEquipmentId(dto.equipmentId());
        issue.setReportedBy(dto.reportedBy());
        issue.setIssueType(dto.issueType());
        issue.setIssueDescription(dto.issueDescription());
        issue.setDetails(dto.details());
        issue.setSeverity(dto.severity());
        issue.setUpdatedBy(dto.userName());

        EquipmentIssue savedIssue = equipmentIssueRepository.save(issue);

        return mapIssueToDto(savedIssue);
    }

    @Override
    @Transactional
    public EquipmentIssueResponseDto updateStatus(Long id, EquipmentIssueStatusDto dto){
        EquipmentIssue issue = equipmentIssueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id: "+id));

        issue.setIssueStatus(dto.issueStatus());
        issue.setUpdatedBy(dto.userName());

        EquipmentIssue savedIssue = equipmentIssueRepository.save(issue);

        return mapIssueToDto(savedIssue);
    }

    @Override
    @Transactional
    public EquipmentIssueResponseDto parentIssue(Long id, EquipmentIssueParentDto dto){
        EquipmentIssue issue = equipmentIssueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id: "+id));

        issue.setParentIssueId(dto.parentIssueId());
        issue.setUpdatedBy(dto.userName());

        EquipmentIssue savedIssue = equipmentIssueRepository.save(issue);

        return mapIssueToDto(savedIssue);
    }

    @Override
    @Transactional
    public EquipmentIssueResponseDto findById(Long id){
        EquipmentIssue issue = equipmentIssueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found by ID: "+id));

        return mapIssueToDto(issue);
    }

    @Override
    @Transactional
    public List<EquipmentIssueResponseDto> findByEquipmentId(Long equipmentId){
        return equipmentIssueRepository.findByEquipmentId(equipmentId)
                .stream()
                .map(this::mapIssueToDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteIssue(Long id){
        if(!equipmentIssueRepository.existsById(id)){
            throw new EntityNotFoundException("Id not found");
        }

        equipmentIssueRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<EquipmentIssueSummaryDto>> getActiveIssuesByEquipmentIds(List<Long> equipmentIds) {
        if (equipmentIds == null || equipmentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<EquipmentIssueProjection> rawIssues = equipmentIssueRepository.findActiveIssuesByEquipmentIds(equipmentIds);

        return rawIssues.stream()
                .map(this::mapToDto)
                .collect(Collectors.groupingBy(EquipmentIssueSummaryDto::equipmentId));
    }

    private EquipmentIssueSummaryDto mapToDto(EquipmentIssueProjection p) {
        return new EquipmentIssueSummaryDto(
                p.getId(),
                p.getEquipmentId(),
                p.getReportedBy(),
                p.getReportedAt(),
                p.getIssueDescription(),
                p.getSeverity(),
                p.getIssueStatus(),
                p.getWorkOrderId(),
                p.getOrderType(),
                p.getOrderStatus()
        );
    }

    private EquipmentIssueResponseDto mapIssueToDto(EquipmentIssue entity){
        Long workOrderId = Optional.ofNullable(entity.getWorkOrder())
                .map(WorkOrder::getId)
                .orElse(null);

        return new EquipmentIssueResponseDto(
                entity.getId(),
                entity.getEquipmentId(),
                entity.getReferenceId(),
                entity.getReportedBy(),
                entity.getReportedAt(),
                entity.getIssueType(),
                entity.getIssueDescription(),
                entity.getDetails(),
                entity.getSeverity(),
                entity.getIssueStatus(),
                workOrderId,
                entity.getParentIssueId()
        );
    }
}
