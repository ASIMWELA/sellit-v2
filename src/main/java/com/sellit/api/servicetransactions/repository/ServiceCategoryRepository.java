package com.sellit.api.servicetransactions.repository;

import com.sellit.api.servicetransactions.entity.ServiceCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategoryEntity, Long> {

    Optional<ServiceCategoryEntity> findByServiceCategoryName(String serviceName);
    boolean existsByServiceCategoryName(String serviceCategoryName);

   Optional<ServiceCategoryEntity> findByUuid(String uuid);
}
