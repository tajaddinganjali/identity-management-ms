package com.management.repository;

import com.management.model.OtpDefinition;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpDefinitionRepository extends JpaRepository<OtpDefinition, UUID> {

}
