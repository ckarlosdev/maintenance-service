package com.hmbrandt.maintenance_service.service.service;

import com.hmbrandt.maintenance_service.dto.service.ServiceRequestDto;
import com.hmbrandt.maintenance_service.dto.service.ServiceResponseDto;
import com.hmbrandt.maintenance_service.entity.service.ServiceType;
import com.hmbrandt.maintenance_service.repository.service.ServiceTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceTypeServiceImpl implements ServiceTypeService {

    private final ServiceTypeRepository serviceRepository;

    public ServiceResponseDto saveService(ServiceRequestDto serviceDto){
        String userName = getUser();
        LocalDateTime now = LocalDateTime.now();

        ServiceType serviceType = new ServiceType();
        serviceType.setServiceName(serviceDto.serviceName());
        serviceType.setCreatedBy(userName);
        serviceType.setCreatedAt(now);

        ServiceType savedServiceType = serviceRepository.save(serviceType);

        return mapServiceToDto(savedServiceType);
    }

    public List<ServiceResponseDto> getAllServices(){
        List<ServiceType> services = serviceRepository.findAll();

        return services.stream()
                .map(this::mapServiceToDto)
                .toList();
    }

    public ServiceResponseDto updateService(ServiceRequestDto serviceDto, Long serviceId){
        String userName = getUser();

        ServiceType service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Service not found, id: "+ serviceId));

        service.setServiceName(serviceDto.serviceName());
        ServiceType updatedService = serviceRepository.save(service);

        return mapServiceToDto(updatedService);
    }

    private ServiceResponseDto mapServiceToDto(ServiceType service){
        return new ServiceResponseDto(
                service.getId(),
                service.getServiceName()
        );
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
}
