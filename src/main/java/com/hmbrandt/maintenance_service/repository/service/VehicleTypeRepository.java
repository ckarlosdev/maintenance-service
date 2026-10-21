package com.hmbrandt.maintenance_service.repository.service;

import com.hmbrandt.maintenance_service.entity.service.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {

}
