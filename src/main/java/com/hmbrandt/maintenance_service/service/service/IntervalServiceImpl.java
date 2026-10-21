package com.hmbrandt.maintenance_service.service.service;

import com.hmbrandt.maintenance_service.dto.service.IntervalRequestDto;
import com.hmbrandt.maintenance_service.dto.service.IntervalResponseDto;
import com.hmbrandt.maintenance_service.entity.service.ServiceInterval;
import com.hmbrandt.maintenance_service.entity.service.ServiceType;
import com.hmbrandt.maintenance_service.entity.service.VehicleType;
import com.hmbrandt.maintenance_service.repository.service.ServiceIntervalRepository;
import com.hmbrandt.maintenance_service.repository.service.ServiceTypeRepository;
import com.hmbrandt.maintenance_service.repository.service.VehicleTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IntervalServiceImpl implements IntervalService{

    private final ServiceIntervalRepository intervalRepository;
    private final ServiceTypeRepository typeRepository;
    private final VehicleTypeRepository vehicleRepository;

    @Override
    public IntervalResponseDto saveInterval(IntervalRequestDto intervalDto){

        String userName = getUser();
        LocalDateTime now = LocalDateTime.now();

        VehicleType vehicleType = vehicleRepository.findById(intervalDto.vehicleTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle type not found with id: "+intervalDto.vehicleTypeId()));
        ServiceType serviceType = typeRepository.findById(intervalDto.serviceId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: "+intervalDto.serviceId()));

        ServiceInterval interval = new ServiceInterval();
        interval.setVehicleType(vehicleType);
        interval.setServiceType(serviceType);
        interval.setIntervalValue(intervalDto.value());
        interval.setIntervalUnit(intervalDto.unit());
        interval.setCreatedBy(userName);
        interval.setCreatedAt(now);
        interval.setUpdatedBy(userName);
        interval.setUpdatedAt(now);

        ServiceInterval newInterval = intervalRepository.save(interval);
        return mapIntervalToDto(newInterval);
    }

    @Override
    public List<IntervalResponseDto> getAll(){
        List<ServiceInterval> intervals = intervalRepository.findAll();

        return intervals.stream()
                .map(this::mapIntervalToDto)
                .toList();
    }

    @Override
    public List<IntervalResponseDto> getByVehicleTypeId(Long typeId){

        List<ServiceInterval> intervals = intervalRepository.findByVehicleType_Id(typeId);
        return intervals.stream()
                .map(this::mapIntervalToDto)
                .toList();
    }

    @Override
    public IntervalResponseDto udpateInterval(IntervalRequestDto intervalDto, Long intervalId){
        String userName = getUser();
        LocalDateTime now = LocalDateTime.now();

        ServiceInterval interval = intervalRepository.findById(intervalId)
                .orElseThrow(() -> new EntityNotFoundException("Interval data not found with id: "+intervalId));

        VehicleType vehicleType = vehicleRepository.findById(intervalDto.vehicleTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle type not found with id: "+intervalDto.vehicleTypeId()));
        ServiceType serviceType = typeRepository.findById(intervalDto.serviceId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: "+intervalDto.serviceId()));

        interval.setVehicleType(vehicleType);
        interval.setServiceType(serviceType);
        interval.setIntervalValue(intervalDto.value());
        interval.setIntervalUnit(intervalDto.unit());
        interval.setCreatedBy(userName);
        interval.setCreatedAt(now);
        interval.setUpdatedBy(userName);
        interval.setUpdatedAt(now);

        ServiceInterval newInterval = intervalRepository.save(interval);
        return mapIntervalToDto(newInterval);
    }

    private IntervalResponseDto mapIntervalToDto(ServiceInterval newInterval){
        return new IntervalResponseDto(
                newInterval.getId(),
                newInterval.getVehicleType().getId(),
                newInterval.getServiceType().getId(),
                newInterval.getIntervalValue(),
                newInterval.getIntervalUnit()
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
