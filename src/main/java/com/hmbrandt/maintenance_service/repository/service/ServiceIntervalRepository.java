package com.hmbrandt.maintenance_service.repository.service;

import com.hmbrandt.maintenance_service.entity.service.ServiceInterval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceIntervalRepository extends JpaRepository<ServiceInterval, Long> {
    List<ServiceInterval> findByVehicleType_Id(Long vehicleTypeId);
}
