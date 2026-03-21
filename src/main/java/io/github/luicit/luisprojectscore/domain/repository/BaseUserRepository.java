package io.github.luicit.luisprojectscore.domain.repository;

import io.github.luicit.luisprojectscore.domain.entity.BaseUserEntity;

import java.util.Optional;

public interface BaseUserRepository<E extends BaseUserEntity> extends DatabaseRepository<E, Long> {

    Optional<E> findByEmailAndDeletedAtIsNull(String email);

    Optional<E> findByUsernameAndDeletedAtIsNull(String username);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    boolean existsByUsernameAndDeletedAtIsNull(String username);

}
