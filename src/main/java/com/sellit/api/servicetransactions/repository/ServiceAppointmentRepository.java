package com.sellit.api.servicetransactions.repository;

import com.sellit.api.servicetransactions.entity.ServiceAppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceAppointmentRepository  extends JpaRepository<ServiceAppointmentEntity, Long> {
    Optional<ServiceAppointmentEntity> findByUuid(String serviceAppointmentUuid);
}
