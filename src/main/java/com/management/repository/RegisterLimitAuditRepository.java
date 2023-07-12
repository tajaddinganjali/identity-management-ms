package com.management.repository;

import com.management.model.RegisterLimitAud;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterLimitAuditRepository extends JpaRepository<RegisterLimitAud, UUID> {

    List<RegisterLimitAud> findAllByPinAndPhoneAndCreatedDateAfter(String pin, String phone, Date date);

}
