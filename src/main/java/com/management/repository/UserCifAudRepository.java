package com.management.repository;

import com.management.model.UserCifAud;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCifAudRepository extends JpaRepository<UserCifAud, UUID> {

}
