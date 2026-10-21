package com.hmbrandt.maintenance_service.controller.service;

import com.hmbrandt.maintenance_service.dto.service.IntervalRequestDto;
import com.hmbrandt.maintenance_service.dto.service.IntervalResponseDto;
import com.hmbrandt.maintenance_service.service.service.IntervalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/maintenance/interval")
@RequiredArgsConstructor
@Tag(name = "Interval Services", description = "API to control the interval service vehicles")
public class IntervalController {
    
    private final IntervalService intervalService;

    @Operation(summary = "Save a new interval", description = "create a new interval for a vehicle.")
    @PostMapping
    public ResponseEntity<IntervalResponseDto> saveInterval(
            @RequestBody IntervalRequestDto intervalDto
            ){
        return new ResponseEntity<>(intervalService.saveInterval(intervalDto), HttpStatus.CREATED);
    }

    @Operation(summary = "update and interval", description = "Update an intervale data by id.")
    @PutMapping("/{intervalId}")
    public ResponseEntity<IntervalResponseDto> updateInterval(
            @RequestBody IntervalRequestDto intervalDto,
            @PathVariable Long intervalId
    ){
        IntervalResponseDto interval = intervalService.udpateInterval(intervalDto, intervalId);
        return ResponseEntity.ok(interval);
    }

    @Operation(summary = "Find intervals", description = "Find intervals by id.")
    @GetMapping("/{vehicleId}")
    public ResponseEntity<List<IntervalResponseDto>> findByVehicleTypeId(
            @PathVariable Long vehicleId
    ){
        return ResponseEntity.ok(intervalService.getByVehicleTypeId(vehicleId));
    }

}
