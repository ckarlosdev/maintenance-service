package com.hmbrandt.maintenance_service.dto.service;

public record IntervalResponseDto(
        Long intervalId,
        Long vehicleTypeId,
        Long serviceId,
        Integer value,
        String unit
) {}
