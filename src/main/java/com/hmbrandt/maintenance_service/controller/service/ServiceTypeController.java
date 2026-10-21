package com.hmbrandt.maintenance_service.controller.service;

import com.hmbrandt.maintenance_service.dto.service.ServiceRequestDto;
import com.hmbrandt.maintenance_service.dto.service.ServiceResponseDto;
import com.hmbrandt.maintenance_service.entity.service.ServiceType;
import com.hmbrandt.maintenance_service.service.service.ServiceTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/maintenance/services")
@RequiredArgsConstructor
@Tag(name = "Vehicle Services", description = "API to control the service type for vehicles")
public class ServiceTypeController {

    private final ServiceTypeService typeService;

    @Operation(summary = "Save a checklist", description = "When a user save a checklist.")
    @PostMapping
    public ResponseEntity<ServiceResponseDto> saveService(@RequestBody ServiceRequestDto serviceDto){
        return new ResponseEntity<>(typeService.saveService(serviceDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Save a checklist", description = "When a user save a checklist.")
    @PutMapping("/{serviceId}")
    public ResponseEntity<ServiceResponseDto> updateService(
            @RequestBody ServiceRequestDto serviceDto,
            @PathVariable Long serviceId
    ){
        ServiceResponseDto service = typeService.updateService(serviceDto, serviceId);
        return ResponseEntity.ok(service);
    }

    @Operation(summary = "get the services data", description = "End point to get the service list.")
    @GetMapping
    public ResponseEntity<List<ServiceResponseDto>> getServices(){
        return ResponseEntity.ok(typeService.getAllServices());
    }
}
