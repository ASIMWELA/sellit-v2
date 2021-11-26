package com.sellit.api.servicetransactions.repository;

import com.sellit.api.servicetransactions.entity.ServiceDeliveryOfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceDeliveryOfferRepository extends JpaRepository<ServiceDeliveryOfferEntity, Long> {
     Optional<ServiceDeliveryOfferEntity> findByUuid(String serviceDeliveryOfferUuid);
}
