package com.management.repository;

import com.management.model.FixOtpDefinition;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixOtpDefinitionRepository extends JpaRepository<FixOtpDefinition, UUID> {

    Optional<FixOtpDefinition> findFirstByPin(String pin);

}
