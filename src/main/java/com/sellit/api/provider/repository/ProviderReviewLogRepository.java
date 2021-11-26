package com.sellit.api.provider.repository;

import com.sellit.api.provider.entity.ProviderReviewLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderReviewLogRepository extends JpaRepository<ProviderReviewLogEntity, Long> {
    Optional<ProviderReviewLogEntity> findByUuid(String proverReviewLogUuid);
}
