package com.sellit.api.user.repository;

import com.sellit.api.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserName(String userName);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
    boolean existsByMobileNumber(String mobileNumber);
    Optional<UserEntity> findByUuid(String uuid);

}
