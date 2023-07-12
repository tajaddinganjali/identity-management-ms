package com.management.service;

import com.management.identity.enums.ProfileType;
import com.management.model.Referral;
import com.management.model.User;
import com.management.model.UserCif;
import com.management.model.UserRegistration;
import com.management.model.enums.ReferralStatus;
import com.management.model.enums.UserStatus;
import com.management.otp.service.CacheService;
import com.management.register.dto.internal.ReferralInfoDTO;
import com.management.register.enums.ReferralType;
import com.management.register.service.impl.ReferralServiceImpl;
import com.management.repository.ReferralRepository;
import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferralServiceUnitTest {

    @Mock
    private CacheService cacheService;
    @Mock
    private ReferralRepository referralRepository;
    @InjectMocks
    private ReferralServiceImpl referralService;

    @Test
    void save() {
        Mockito.when(referralRepository.save(Mockito.any())).thenReturn(Referral.builder().build());
        Assertions.assertDoesNotThrow(() -> referralService.save(Referral.builder()
                .referralType(ReferralType.BANK_REPRESENTATIVE)
                .referredBy("test")
                .build()));
    }

    @Test
    void checkAndAddReferralIfValid() {
        ReferralInfoDTO referralInfo = ReferralInfoDTO.builder()
                .referralType(ReferralType.BANK_REPRESENTATIVE)
                .referredBy("test").build();
        findByReferredUser();
        Assertions.assertDoesNotThrow(() -> referralService.checkAndAddReferralIfValid("ibamobile", referralInfo,
                getUser()));
    }

    @Test
    void checkAndAddReferralIfValid_emptyReferral() {
        ReferralInfoDTO referralInfo = ReferralInfoDTO.builder()
                .referralType(ReferralType.BANK_REPRESENTATIVE)
                .referredBy("test").build();
        Assertions.assertDoesNotThrow(() -> referralService.checkAndAddReferralIfValid("ibamobile", referralInfo,
                getUser()));
    }

    @Test
    void checkAndAddReferralIfValid_invalidReferral() {
        ReferralInfoDTO referralInfo = ReferralInfoDTO.builder()
                .referralType(ReferralType.BANK_REPRESENTATIVE)
                .referredBy("test").build();
        Assertions.assertDoesNotThrow(() -> referralService.checkAndAddReferralIfValid("ibamobile", referralInfo,
                User.builder().registrations(List.of(UserRegistration.builder().consumer("ibamobile").password("pass").build())).build()));
    }

    @Test
    void updateReferral() {
        Mockito.when(referralRepository.updateReferralStatus(Mockito.any())).thenReturn(1);
        Assertions.assertDoesNotThrow(() -> referralService.updateReferralStatus(getUser().getId()));
    }

    @Test
    void findByReferredUser() {
        Mockito.when(referralRepository.findByReferredUser(Mockito.any())).thenReturn(Optional.of(
                Referral.builder().status(ReferralStatus.PENDING)
                        .referralType(ReferralType.BANK_REPRESENTATIVE).referredBy("test").build()));
        Assertions.assertDoesNotThrow(() -> referralService.findByReferredUser(getUser()));
    }

    private User getUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .age(1)
                .cardNumber("card")
                .cif("cif")
                .profileType(ProfileType.CUSTOMER)
                .gender("gender")
                .identityIssuingDate("date")
                .identityMaturityDate("date")
                .identityNumber("number")
                .identityType("type")
                .identityIssuingDate("type")
                .maritalStatus("status")
                .dateOfBirth(Date.valueOf(LocalDate.now()))
                .name("name")
                .passcode("code")
                .phone("phone")
                .pin("pin")
                .status(UserStatus.ACTIVE)
                .version(1L)
                .surname("surname")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .resident(true)
                .cifs(List.of(UserCif.builder().cif("cif").build()))
                .registrations(List.of(UserRegistration.builder().consumer("id").build()))
                .build();
    }

}
