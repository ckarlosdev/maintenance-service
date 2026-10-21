package com.hmbrandt.maintenance_service.dto.service;

public record IntervalRequestDto(
        Long vehicleTypeId,
        Long serviceId,
        Integer value,
        String unit
) {}
