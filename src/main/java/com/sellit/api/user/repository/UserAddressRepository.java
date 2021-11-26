package com.sellit.api.user.repository;

import com.sellit.api.user.entity.UserAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddressEntity, Long> {
}
