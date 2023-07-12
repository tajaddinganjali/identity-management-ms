package com.management.limit.strategy.impl;

import com.management.exception.GeneralTechErrorEnum;
import com.management.exception.GeneralTechnicalException;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.limit.strategy.LimitServiceStrategy;
import com.management.model.User;
import com.management.repository.UserRepository;
import java.util.Optional;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service("login_check_passcode")
public class PasscodeLimitServiceStrategyImpl implements LimitServiceStrategy {

    private static final String KEY_VALUE_FORMATTER = "%s_%s";

    private final RMapCache<String, Integer> passcodeLimitCache;
    private final RMapCache<String, Integer> userLockCache;
    private final RMapCache<String, Integer> userLockDurationCache;
    private final UserRepository userRepository;

    public PasscodeLimitServiceStrategyImpl(RedissonClient redissonClient, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userLockCache = redissonClient.getMapCache("userblock");
        this.userLockDurationCache = redissonClient.getMapCache("userblockduration");
        this.passcodeLimitCache = redissonClient.getMapCache("passcodelimit");
    }

    @Override
    public void checkLimit(String pin, LimitServiceTypeEnum serviceName) {
        throw new GeneralTechnicalException(GeneralTechErrorEnum.GENERAL_SERVER_ERROR, "not implemented");
    }

    @Override
    public void doSuccessAttempt(String pin, LimitServiceTypeEnum serviceName) {
        Optional<User> optionalUser = userRepository.findByPin(pin);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            passcodeLimitCache.remove(user.getId().toString());
            String userLockKey = formatUserLockKey(user.getId().toString());
            userLockDurationCache.remove(userLockKey);
            unlockUser(user.getId().toString());
        }
    }

    private void unlockUser(String userId) {
        String userAttemptKey = formatUserAttemptKey(userId);
        userLockCache.remove(userAttemptKey);
    }

    private String formatUserAttemptKey(String userId) {
        return String.format(KEY_VALUE_FORMATTER, userId, "passcodeattempt");
    }

    private String formatUserLockKey(String userId) {
        return String.format(KEY_VALUE_FORMATTER, userId, "passcodelock");
    }

}
