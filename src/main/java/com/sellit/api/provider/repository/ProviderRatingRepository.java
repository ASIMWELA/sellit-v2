package com.sellit.api.provider.repository;

import com.sellit.api.provider.entity.ProviderRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRatingRepository extends JpaRepository<ProviderRatingEntity, Long> {
}
