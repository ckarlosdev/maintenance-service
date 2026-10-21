package com.hmbrandt.maintenance_service.service.service;


import com.hmbrandt.maintenance_service.dto.service.IntervalRequestDto;
import com.hmbrandt.maintenance_service.dto.service.IntervalResponseDto;

import java.util.List;

public interface IntervalService {
    //create
    IntervalResponseDto saveInterval(IntervalRequestDto intervalDto);
    //read
    List<IntervalResponseDto> getAll();
    List<IntervalResponseDto> getByVehicleTypeId(Long typeId);
    //update
    IntervalResponseDto udpateInterval(IntervalRequestDto intervalDto, Long intervalId);
}
