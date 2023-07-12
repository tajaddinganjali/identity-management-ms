package com.management.repository;

import com.management.model.User;
import com.management.model.UserCif;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserCifRepository extends JpaRepository<UserCif, UUID> {

    Optional<UserCif> findByUserAndCif(User user, String cif);

    List<UserCif> findByUser(User user);

    @Modifying
    @Transactional
    @Query("delete from UserCif uc where uc.id=:id")
    @Override
    void deleteById(UUID id);

    @Query("select uc from UserCif uc "
            + "where uc.id in (select distinct (cp.cif) from CifPhone cp where cp.phone=:phone)")
    List<UserCif> getCifsByPhone(String phone);

}
