package com.hmbrandt.maintenance_service.controller;

import com.hmbrandt.maintenance_service.dto.*;
import com.hmbrandt.maintenance_service.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v2/maintenance/work-order")
@RequiredArgsConstructor
@Tag(name = "Work Order", description = "ServiceRepository to control the work orders equipment maintenance ")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @Operation(summary = "Create a Work Order", description = "End point to create a new work order.")
    @PostMapping
    public ResponseEntity<WorkOrderResponseDto>  createOrder(
            @Valid @RequestBody WorkOrderRequestDto workOrderDto
    ){
        return new ResponseEntity<>(workOrderService.createOrder(workOrderDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Add new task", description = "Add a new task for a Work Order.")
    @PostMapping("/{workOrderId}/tasks")
    public ResponseEntity<WorkOrderTaskResponseDto> addTask(
            @PathVariable Long workOrderId,
            @Valid @RequestBody WorkOrderTaskRequestDto taskDto
    ){
        return new ResponseEntity<>(workOrderService.addTask(workOrderId, taskDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a task", description = "update a work order task.")
    @PatchMapping("/task/{taskId}")
    public ResponseEntity<WorkOrderTaskResponseDto> patchTask(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateWorkOrderTaskDto dto
    ) {
        WorkOrderTaskResponseDto response = workOrderService.updateTask(taskId, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete task", description = "delete task by an id provided")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        workOrderService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update work order status", description = "update status/close orders")
    @PatchMapping("{id}/status")
    public ResponseEntity<WorkOrderResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkOrderStatusDto taskDto
    ){
        WorkOrderResponseDto updatedOrder = workOrderService.updateStatus(id, taskDto);
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "update work order", description = "update work orders (general fields)")
    @PatchMapping("{id}")
    public ResponseEntity<WorkOrderResponseDto> updateWorkOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkOrderDto dto
    ) {
        WorkOrderResponseDto updatedOrder = workOrderService.updateWorkOrder(id, dto);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<WorkOrderResponseDto>> getWorkOrderByEquipmentId(
            @PathVariable Long equipmentId
    ){
        return ResponseEntity.ok(
                workOrderService.findWorkOrdersByEquipmentId(equipmentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.findById(id));
    }

    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDTO> getMetrics() {
        return ResponseEntity.ok(workOrderService.getMetrics());
    }

    @GetMapping("/details")
    public ResponseEntity<List<KpiDetailResponseDto>> getKpiDetails(
            @RequestParam KpiType type,
            @RequestParam(required = false) Long equipmentId) {

        List<KpiDetailResponseDto> response = workOrderService.getKpiDetails(type, equipmentId);
        return ResponseEntity.ok(response);
    }
}
