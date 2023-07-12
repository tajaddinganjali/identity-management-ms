package com.management.repository;

import com.management.model.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * This repository was designed to be boilerplate for all repositories covered in this microservice.
 *
 * @param <T> generic for defining entity that is going to be handled by repository
 */
@NoRepositoryBean
public interface DefaultRepository<T extends AbstractEntity> extends JpaRepository<T, Long> {

}
