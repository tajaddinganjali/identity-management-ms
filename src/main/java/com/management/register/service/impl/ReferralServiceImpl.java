package com.management.register.service.impl;

import com.management.model.Referral;
import com.management.model.User;
import com.management.model.UserRegistration;
import com.management.model.enums.ReferralStatus;
import com.management.otp.service.CacheService;
import com.management.register.dto.internal.ReferralInfoDTO;
import com.management.register.service.ReferralService;
import com.management.repository.ReferralRepository;
import com.management.util.constant.ConsumerConstant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReferralServiceImpl implements ReferralService {

    private final CacheService cacheService;
    private final ReferralRepository referralRepository;

    @Override
    public void save(Referral referral) {
        referralRepository.save(referral);
    }

    @Override
    public void checkAndAddReferralIfValid(String consumerId, ReferralInfoDTO referralRequest, User user) {
        if (consumerId.equals(ConsumerConstant.MOBILE)
                && Objects.nonNull(referralRequest)
                && Objects.nonNull(referralRequest.getReferralType())
                && StringUtils.isNotBlank(referralRequest.getReferredBy())) {

            boolean isValidReferral = true;

            for (UserRegistration registration : user.getRegistrations()) {
                if (registration.getConsumer().equals(ConsumerConstant.MOBILE)
                        && StringUtils.isNotBlank(registration.getPassword())) {
                    isValidReferral = false;
                }
            }

            if (isValidReferral) {
                Optional<Referral> referralOptional = findByReferredUser(user);
                saveOrUpdateReferral(referralRequest, referralOptional, user);
            }
        }
    }

    @Override
    public int updateReferralStatus(UUID userId) {
        return referralRepository.updateReferralStatus(userId);
    }

    public Optional<Referral> findByReferredUser(User user) {
        return referralRepository.findByReferredUser(user);
    }

    private void saveOrUpdateReferral(ReferralInfoDTO referralRequest, Optional<Referral> referralOptional,
                                      User user) {
        Referral referral;
        if (referralOptional.isPresent()) {
            referral = referralOptional.get();
            if (referral.getStatus() == ReferralStatus.ACTIVE) {
                return;
            }
            referral.setStatus(ReferralStatus.PENDING);
            referral.setReferralType(referralRequest.getReferralType());
            referral.setReferredBy(referralRequest.getReferredBy());
            referral.setReferredUser(user);
        } else {
            referral = Referral.builder()
                    .status(ReferralStatus.PENDING)
                    .referralType(referralRequest.getReferralType())
                    .referredBy(referralRequest.getReferredBy())
                    .referredUser(user)
                    .build();
        }
        save(referral);
        cacheService.saveReferralToCache(user.getId().toString(), referralRequest);
    }

}
