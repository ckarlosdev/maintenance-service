package com.hmbrandt.maintenance_service.service.service;

import com.hmbrandt.maintenance_service.dto.service.ServiceRequestDto;
import com.hmbrandt.maintenance_service.dto.service.ServiceResponseDto;

import java.util.List;

public interface ServiceTypeService {
    // CREATE
    ServiceResponseDto saveService(ServiceRequestDto serviceDto);
    // READ
    List<ServiceResponseDto> getAllServices();
    // UPDATE
    ServiceResponseDto updateService(ServiceRequestDto serviceDto, Long serviceId);
}
