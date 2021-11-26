package com.sellit.api.provider.repository;

import com.sellit.api.provider.entity.ServiceProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProviderEntity, Long> {

    Optional<ServiceProviderEntity> findByUuid(String uuid);
}
