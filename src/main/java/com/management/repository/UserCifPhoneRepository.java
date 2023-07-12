package com.management.repository;

import com.management.model.CifPhone;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserCifPhoneRepository extends JpaRepository<CifPhone, UUID> {

    List<CifPhone> findByCifId(UUID id);

    @Modifying
    @Transactional
    @Query("delete from CifPhone c where c.id=:id")
    @Override
    void deleteById(UUID id);

}
