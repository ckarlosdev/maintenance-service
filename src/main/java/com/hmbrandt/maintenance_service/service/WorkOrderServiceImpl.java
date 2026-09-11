package com.hmbrandt.maintenance_service.service;

import com.hmbrandt.maintenance_service.dto.*;
import com.hmbrandt.maintenance_service.entity.EquipmentIssue;
import com.hmbrandt.maintenance_service.entity.PreventiveSchedule;
import com.hmbrandt.maintenance_service.entity.WorkOrder;
import com.hmbrandt.maintenance_service.entity.WorkOrderTask;
import com.hmbrandt.maintenance_service.repository.EquipmentIssueRepository;
import com.hmbrandt.maintenance_service.repository.PreventiveScheduleRepository;
import com.hmbrandt.maintenance_service.repository.WorkOrderRepository;
import com.hmbrandt.maintenance_service.repository.WorkOrderTaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderTaskRepository workOrderTaskRepository;
    private final EquipmentIssueRepository equipmentIssueRepository;
    private final PreventiveScheduleRepository preventiveScheduleRepository;

    @Override
    @Transactional
    public WorkOrderResponseDto createOrder(WorkOrderRequestDto dto){

        WorkOrder newWorkOrder = new WorkOrder();
        newWorkOrder.setEquipmentId(dto.equipmentId());
        newWorkOrder.setOrderType(dto.orderType());
        newWorkOrder.setOrderStatus("DRAFT");
        newWorkOrder.setCreatedBy(dto.createdBy());
        newWorkOrder.setUpdatedBy(dto.createdBy());


        if(dto.tasks() != null){
            List<WorkOrderTask> tasks = dto.tasks().stream()
                    .map(taskDto -> {
                        WorkOrderTask task =  new WorkOrderTask();
                        task.setWorkOrder(newWorkOrder);
                        task.setTaskDescription(taskDto.taskDescription());
                        task.setPreventivePlanId(taskDto.preventivePlanId());
                        task.setEquipmentIssueId(taskDto.equipmentIssueId());
                        task.setCreatedBy(dto.createdBy());
                        task.setUpdatedBy(dto.createdBy());
                        return task;
                    })
                    .toList();
            newWorkOrder.setTaks(tasks);
        }

        WorkOrder savedWorkOrder = workOrderRepository.save(newWorkOrder);

        if (dto.tasks() != null) {
            dto.tasks().stream()
                    .map(WorkOrderTaskRequestDto::equipmentIssueId) // Ajusta el nombre de la propiedad en tu DTO
                    .filter(Objects::nonNull)
                    .distinct()
                    .forEach(issueId -> {
                        EquipmentIssue issue = equipmentIssueRepository.findById(issueId)
                                .orElseThrow(() -> new EntityNotFoundException("Equipment Issue not found: " + issueId));

                        issue.setWorkOrder(savedWorkOrder);
                        issue.setUpdatedBy(dto.createdBy());
                        issue.setIssueStatus("IN_PROGRESS");

                        equipmentIssueRepository.save(issue);
                    });
        }

        return mapWorkOrderToDto(savedWorkOrder);
    }

    @Override
    @Transactional
    public WorkOrderTaskResponseDto addTask(Long workOrderId, WorkOrderTaskRequestDto taskDto){
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Work Order not found, ID: " + workOrderId));

        WorkOrderTask newTask = new WorkOrderTask();
        newTask.setWorkOrder(workOrder);
        newTask.setTaskDescription(taskDto.taskDescription());
        newTask.setPreventivePlanId(taskDto.preventivePlanId());
        newTask.setEquipmentIssueId(taskDto.equipmentIssueId());
        newTask.setCreatedBy(taskDto.createdBy());
        newTask.setUpdatedBy(taskDto.createdBy());

        if (taskDto.equipmentIssueId() != null) {
            equipmentIssueRepository.findById(taskDto.equipmentIssueId()).ifPresent(issue -> {
                issue.setWorkOrder(workOrder);
                issue.setIssueStatus("IN_PROGRESS");
                issue.setUpdatedBy(taskDto.createdBy());
            });
        }

        WorkOrderTask savedTask = workOrderTaskRepository.save(newTask);

        return mapTaskToDto(savedTask);
    }

    @Override
    @Transactional
    public WorkOrderTaskResponseDto updateTask(Long taskId, UpdateWorkOrderTaskDto dto) {
        WorkOrderTask task = workOrderTaskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found, ID: " + taskId));

        // 1. Actualizar campos opcionales si vienen en el DTO
        if (dto.taskDescription() != null) {
            task.setTaskDescription(dto.taskDescription());
        }

        if (dto.preventivePlanId() != null) {
            task.setPreventivePlanId(dto.preventivePlanId());
        }

        if (dto.equipmentIssueId() != null) {
            task.setEquipmentIssueId(dto.equipmentIssueId());
        }

        // 2. Actualizar el usuario que modifica (AQUÍ ESTABA EL ERROR)
        if (dto.updatedBy() != null) {
            task.setUpdatedBy(dto.updatedBy()); // ✅ Se asigna desde el DTO
        }

        // 3. Manejo del estado de completado
        if (dto.isCompleted() != null) {
            boolean wasCompleted = Boolean.TRUE.equals(task.getIsCompleted());
            boolean nowCompleted = dto.isCompleted();

            task.setIsCompleted(nowCompleted);

            if (nowCompleted && !wasCompleted) {
                task.setCompletedAt(LocalDateTime.now());

                // Si la tarea resuelve un Issue, lo marcamos como RESOLVED
                if (task.getEquipmentIssueId() != null) {
                    equipmentIssueRepository.findById(task.getEquipmentIssueId())
                            .ifPresent(issue -> {
                                issue.setIssueStatus("RESOLVED");
                                // Asignamos el usuario actualizado directamente desde el DTO o del task ya actualizado
                                issue.setUpdatedBy(task.getUpdatedBy());
                            });
                }

            } else if (!nowCompleted && wasCompleted) {
                // Se desmarcó como completada -> Limpiamos fecha
                task.setCompletedAt(null);
            }
        }

        // 4. Guardar cambios
        WorkOrderTask updatedTask = workOrderTaskRepository.save(task);

        return mapTaskToDto(updatedTask);
    }

    @Override
    @Transactional
    public WorkOrderResponseDto findById(Long id){
        return workOrderRepository.findById(id)
                .map(this::mapWorkOrderToDto)
                .orElseThrow(() -> new EntityNotFoundException("Work order not fount with id: " + id));
    }

    @Override
    @Transactional
    public List<WorkOrderResponseDto> findWorkOrdersByEquipmentId(Long equipmentId){
        return workOrderRepository.findByEquipmentId(equipmentId)
                .stream()
                .map(this::mapWorkOrderToDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId){
        if(!workOrderTaskRepository.existsById(taskId)){
            throw new EntityNotFoundException("Id not found");
        }

        workOrderTaskRepository.deleteById(taskId);
    }

    @Override
    @Transactional
    public WorkOrderResponseDto updateStatus(Long id, UpdateWorkOrderStatusDto dto) {

        // 1. Buscar la orden de trabajo existente
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Work Order not found with ID: " + id));

        String newStatus = dto.orderStatus().toUpperCase();
        String currentStatus = workOrder.getOrderStatus();

        // 2. Validar que no se intente modificar una orden que ya está CERRADA/CANCELADA (opcional)
        if ("CLOSED".equals(currentStatus) || "CANCELLED".equals(currentStatus)) {
            throw new IllegalStateException("Cannot update status of a work order that is already " + currentStatus);
        }

        // 3. Actualizar el estado
        workOrder.setOrderStatus(newStatus);

        // 4. Reglas de negocio según el nuevo estado
        if ("CLOSED".equals(newStatus)) {
            // Se marca la fecha y hora de cierre
            workOrder.setClosedAt(LocalDateTime.now());
            workOrder.setUpdatedBy(dto.userName());

            // Si el cliente envía un costo total final, lo asignamos
            if (dto.totalCost() != null) {
                workOrder.setTotalCost(dto.totalCost());
            }

            if (dto.nextSchedules() != null && !dto.nextSchedules().isEmpty()) {
                for (NextScheduleInputDto scheduleInput : dto.nextSchedules()) {
                    updateNextPreventiveSchedule(workOrder.getEquipmentId(), scheduleInput);
                }
            }

        } else if ("IN_PROGRESS".equals(newStatus) && workOrder.getClosedAt() != null) {
            // Si por alguna razón la reabren, limpiamos la fecha de cierre
            workOrder.setClosedAt(null);
        }

        // 5. Registrar el usuario que realiza la modificación para auditoría
        workOrder.setUpdatedBy(dto.userName());

        // 6. Al estar anotado con @Transactional, JPA persiste los cambios al terminar
        return mapWorkOrderToDto(workOrder);
    }

    private void updateNextPreventiveSchedule(Long equipmentId, NextScheduleInputDto input) {
        preventiveScheduleRepository
                .findByEquipmentIdAndId(equipmentId, input.preventivePlanId())
                .ifPresent(schedule -> {
                    if (input.nextDueDate() != null && !input.nextDueDate().isBlank()) {
                        schedule.setDueDate(LocalDate.parse(input.nextDueDate()));
                    }
                    if (input.nextDueMeter() != null) {
                        schedule.setDueMeter(input.nextDueMeter());
                    }
                    schedule.setLastPerformedDate(LocalDate.now());
                    schedule.setIsOverdue(false);
                    preventiveScheduleRepository.save(schedule);
                });
    }

    @Override
    @Transactional
    public WorkOrderResponseDto updateWorkOrder(Long id, UpdateWorkOrderDto dto) {

        // 1. Buscar la orden de trabajo existente
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Work Order not found with ID: " + id));

        // 2. Regla de seguridad: Evitar editar campos generales si la orden ya está cerrada/cancelada
        if ("CLOSED".equals(workOrder.getOrderStatus()) || "CANCELLED".equals(workOrder.getOrderStatus())) {
            throw new IllegalStateException("Cannot update details of a work order that is already " + workOrder.getOrderStatus());
        }

        // 3. Actualización condicional del equipo asociado
        if (dto.equipmentId() != null) {
            // (Opcional) Validar que el nuevo equipo exista en la BD antes de reasignarlo:
//            boolean equipmentExists = equipmentRepository.existsById(dto.equipmentId());
//            if (!equipmentExists) {
//                throw new EntityNotFoundException("Equipment not found with ID: " + dto.equipmentId());
//            }
            workOrder.setEquipmentId(dto.equipmentId());
        }

        // 4. Actualización del tipo de orden (ej: PREVENTIVE, CORRECTIVE)
        if (dto.orderType() != null && !dto.orderType().isBlank()) {
            workOrder.setOrderType(dto.orderType().toUpperCase());
        }

        // 5. Actualización del costo total
        if (dto.totalCost() != null) {
            workOrder.setTotalCost(dto.totalCost());
        }

        // 6. Registro de usuario para auditoría
        workOrder.setUpdatedBy(dto.userName());

        // 7. Retornar el DTO actualizado (JPA guarda los cambios al salir del método @Transactional)
        return mapWorkOrderToDto(workOrder);
    }

    @Transactional(readOnly = true)
    public DashboardMetricsDTO getMetrics() {
        return workOrderRepository.getDashboardMetrics();
    }

    public List<KpiDetailResponseDto> getKpiDetails(KpiType type, Long equipmentId) {
        switch (type) {
            case PENDING_WOS:
                List<String> pendingStatuses = Arrays.asList("DRAFT", "SCHEDULED", "OPEN");
                var wos = (equipmentId != null)
                        ? workOrderRepository.findByOrderStatusInAndEquipmentIdAndDeletedAtIsNullOrderByCreatedAtDesc(pendingStatuses, equipmentId)
                        : workOrderRepository.findByOrderStatusInAndDeletedAtIsNullOrderByCreatedAtDesc(pendingStatuses);

                return wos.stream().map(wo -> new KpiDetailResponseDto(
                        wo.getId(),
                        wo.getEquipmentId(),
                        "Order #" + wo.getId(),
                        "Type: " + wo.getOrderType(),
                        wo.getOrderStatus(),
                        null,
                        wo.getCreatedAt()
                )).collect(Collectors.toList());

            case DUE_SOON:
                LocalDate limitDate = LocalDate.now().plusDays(30);
                var schedules = (equipmentId != null)
                        ? preventiveScheduleRepository.findDueSoonByEquipment(limitDate, equipmentId)
                        : preventiveScheduleRepository.findDueSoon(limitDate);

                return schedules.stream().map(s -> new KpiDetailResponseDto(
                        s.getId(),
                        s.getEquipmentId(),
                        "Plan #" + s.getPreventivePlanId(),
                        "Meter limit: " + s.getDueMeter(),
                        Boolean.TRUE.equals(s.getIsOverdue()) ? "OVERDUE" : "DUE_SOON",
                        null,
                        s.getDueDate() != null ? s.getDueDate().atStartOfDay() : null
                )).collect(Collectors.toList());

            case IN_PROGRESS:
                var inProgressIssues = (equipmentId != null)
                        ? equipmentIssueRepository.findByIssueStatusAndEquipmentIdAndDeletedAtIsNullOrderByReportedAtDesc("IN_PROGRESS", equipmentId)
                        : equipmentIssueRepository.findByIssueStatusAndDeletedAtIsNullOrderByReportedAtDesc("IN_PROGRESS");

                return inProgressIssues.stream().map(issue -> new KpiDetailResponseDto(
                        issue.getId(),
                        issue.getEquipmentId(),
                        issue.getIssueDescription(),
                        "Reported by: " + issue.getReportedBy(),
                        issue.getIssueStatus(),
                        issue.getSeverity(),
                        issue.getReportedAt()
                )).collect(Collectors.toList());

            case CRITICAL_ISSUES:
                List<String> openStatuses = Arrays.asList("OPEN", "IN_PROGRESS");
                var criticalIssues = (equipmentId != null)
                        ? equipmentIssueRepository.findBySeverityAndIssueStatusInAndEquipmentIdAndDeletedAtIsNullOrderByReportedAtDesc("CRITICAL", openStatuses, equipmentId)
                        : equipmentIssueRepository.findBySeverityAndIssueStatusInAndDeletedAtIsNullOrderByReportedAtDesc("CRITICAL", openStatuses);

                return criticalIssues.stream().map(issue -> new KpiDetailResponseDto(
                        issue.getId(),
                        issue.getEquipmentId(),
                        issue.getIssueDescription(),
                        "Reported by: " + issue.getReportedBy(),
                        issue.getIssueStatus(),
                        issue.getSeverity(),
                        issue.getReportedAt()
                )).collect(Collectors.toList());

            default:
                throw new IllegalArgumentException("Invalid KPI type: " + type);
        }
    }


    private String getUser(){
        String currentUser = "SYSTEM_FALLBACK";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            currentUser = authentication.getName();
        } else {
            System.out.println(">>> ALERT: no authenticated or context NULL");
        }

        return currentUser;
    }

    private WorkOrderResponseDto mapWorkOrderToDto(WorkOrder workOrder){
        return new WorkOrderResponseDto (
                workOrder.getId(),
                workOrder.getEquipmentId(),
                workOrder.getOrderType(),
                workOrder.getOrderStatus(),
                workOrder.getTotalCost(),
                workOrder.getCreatedBy(),
                workOrder.getCreatedAt(),
                workOrder.getTaks()
                        .stream()
                        .map(this::mapTaskToDto)
                        .collect(Collectors.toList())
        );
    }

    private WorkOrderTaskResponseDto mapTaskToDto(WorkOrderTask workOrderTask){
        PreventiveScheduleResponseDto scheduleDto = null;
        if (workOrderTask.getPreventivePlanId() != null) {
            scheduleDto = preventiveScheduleRepository.findById(workOrderTask.getPreventivePlanId())
                    .map(this::mapScheduleToDto) // o usando tu servicio/mapper existente
                    .orElse(null);
        }

        EquipmentIssueResponseDto issueDto = null;
        if (workOrderTask.getEquipmentIssueId() != null) {
            issueDto = equipmentIssueRepository.findById(workOrderTask.getEquipmentIssueId())
                    .map(this::mapIssueToDto) // o usando tu servicio/mapper existente
                    .orElse(null);
        }

        return new WorkOrderTaskResponseDto(
                workOrderTask.getId(),
                workOrderTask.getTaskDescription(),
                scheduleDto,
                issueDto,
                workOrderTask.getIsCompleted()
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

    private PreventiveScheduleResponseDto mapScheduleToDto(PreventiveSchedule entity){
        return new PreventiveScheduleResponseDto(
                entity.getId(),
                entity.getPreventivePlanId(),
                entity.getEquipmentId(),
                entity.getLastPerformedDate(),
                entity.getLastPerformedMeter(),
                entity.getDueDate(),
                entity.getDueMeter(),
                entity.getIsOverdue()
        );
    }
}
