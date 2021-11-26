package com.sellit.api.servicetransactions.repository;

import com.sellit.api.servicetransactions.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    Optional<ServiceEntity> findByServiceName(String serviceName);
    boolean existsByServiceName(String serviceName);
    Optional<ServiceEntity> findByUuid(String uuid);
}
