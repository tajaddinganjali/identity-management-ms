package com.management.register.service;

import com.management.model.Referral;
import com.management.model.User;
import com.management.register.dto.internal.ReferralInfoDTO;
import java.util.UUID;

public interface ReferralService {

    void save(Referral referral);

    void checkAndAddReferralIfValid(String consumerId, ReferralInfoDTO referralRequest,
                                    User user);

    int updateReferralStatus(UUID userId);

}
