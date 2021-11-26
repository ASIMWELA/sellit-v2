package com.sellit.api.provider.repository;

import com.sellit.api.provider.entity.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<ProviderEntity, Long> {

    Optional<ProviderEntity> findByUuid(String uuid);

}
