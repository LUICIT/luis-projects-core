package com.luisraguilar.luisprojectscore.domain.repository;

import com.luisraguilar.luisprojectscore.domain.entity.BaseUserEntity;

import java.util.Optional;

public interface BaseUserRepository extends DatabaseRepository<BaseUserEntity, Long> {

    Optional<BaseUserEntity> findByEmailAndDeletedAtIsNull(String email);
    Optional<BaseUserEntity> findByUsernameAndDeletedAtIsNull(String username);
    boolean existsByEmailAndDeletedAtIsNull(String email);
    boolean existsByUsernameAndDeletedAtIsNull(String username);

}
