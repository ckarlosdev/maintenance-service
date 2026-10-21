package com.hmbrandt.maintenance_service.entity.service;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;

@Entity
@Table(name = "service_intervals", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"vehicle_type_id", "service_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceInterval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_interval_id")
    private Long id;

    // Mapeo como objeto para la relación ManyToOne con VehicleTypeRepository
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    // Mapeo como objeto para la relación ManyToOne con ServiceTypeRepository
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceType serviceType;

    @Column(name = "interval_value", nullable = false)
    private Integer intervalValue;

    @Column(name = "interval_unit", nullable = false, length = 30)
    private String intervalUnit;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
