package com.sellit.api.servicetransactions.repository;

import com.sellit.api.servicetransactions.entity.ServiceRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequestEntity, Long> {
    Optional<ServiceRequestEntity> findByUuid(String serviceRequestUuid);
}
