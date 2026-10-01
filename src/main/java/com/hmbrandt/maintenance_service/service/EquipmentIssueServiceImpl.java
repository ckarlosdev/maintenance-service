package com.hmbrandt.maintenance_service.service;

import com.hmbrandt.maintenance_service.client.NotificationClient;
import com.hmbrandt.maintenance_service.dto.*;
import com.hmbrandt.maintenance_service.dto.Notification.EquipmentDataDto;
import com.hmbrandt.maintenance_service.entity.EquipmentIssue;
import com.hmbrandt.maintenance_service.entity.WorkOrder;
import com.hmbrandt.maintenance_service.repository.EquipmentIssueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentIssueServiceImpl implements EquipmentIssueService {

    private final EquipmentIssueRepository equipmentIssueRepository;
    private final NotificationClient notificationClient;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Value("${application.config.recipients-path}")
    private String recipientsPath;

    @Value("${application.config.templates-path}")
    private String templatesPath;

    @Override
    @Transactional
    public EquipmentIssueResponseDto saveIssue(EquipmentIssueRequestDto dto, EquipmentDataDto equipData){

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
        createNotification(savedIssue, dto.userName(), equipData);

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
                p.getReferenceId(),
                p.getReportedBy(),
                p.getReportedAt(),
                p.getIssueType(),
                p.getIssueDescription(),
                p.getDetails(),
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

    @Override
    public List<EquipmentIssueResponseDto> getReports(){
        return equipmentIssueRepository.findByWorkOrderIsNullOrderByReportedAtDesc()
                .stream()
                .map(this::mapIssueToDto)
                .toList();
    }

    private void createNotification(
            EquipmentIssue savedReport,
            String currentUser,
            EquipmentDataDto equipment
    ) {
        String htmlBody = messageFormat(
                savedReport,
                currentUser,
                equipment
        );

        String recipients = getNotificationRecipients();
        notificationClient.sendEmailNotification(
                recipients,
                "New issue report, Equipment #" + equipment.number(),
                htmlBody
        );
    }

    private String getNotificationRecipients() {
        Path path = Paths.get(recipientsPath);
        try {
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                // Filtra líneas vacías o comentarios (#) y las une separadas por coma
                String recipients = lines.stream()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .reduce((a, b) -> a + "," + b)
                        .orElse("cramirez@hmbrandt.com"); // Fallback si el archivo está vacío
                return recipients;
            }
        } catch (Exception e) {
            log.error("Error reading the recipient file recipients.txt: {}", e.getMessage());
        }
        return "cramirez@hmbrandt.com";
    }


    private String messageFormat(
            EquipmentIssue savedReport,
            String currentUser,
            EquipmentDataDto equipment
    ) {
        var notificationContent = new NotificationText(
                "New Equipment Issue Report",
                "A new equipment issue report has been generated in the system."
        );

        String formattedDate = savedReport.getReportedAt() != null
                ? savedReport.getReportedAt().format(DATE_FORMATTER)
                : "N/A";

        try {
            Path path = Paths.get(templatesPath);
            String template = Files.readString(path);

            return template
                    .replace("${title}", notificationContent.title())
                    .replace("${message}", notificationContent.message())
                    .replace("${equipmentNumber}", String.valueOf(equipment.number()))
                    .replace("${reportId}", String.valueOf(savedReport.getId()))
                    .replace("${equipmentName}", equipment.name())
                    .replace("${reportedAt}", formattedDate)
                    .replace("${reportedBy}", currentUser)
                    .replace("${issue}", String.valueOf(savedReport.getIssueType()))
                    .replace("${details}", String.valueOf(savedReport.getDetails()));

        } catch (Exception e) {
            log.error("Error reading HTML template file: {}", e.getMessage());
            // String de fallback simple si falla la lectura del archivo
            return "<p>" + notificationContent.message() + "</p>";
        }
    }
    private record NotificationText(String title, String message) {}

}
