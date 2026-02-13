package com.elec_business.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class OtpService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    private final RedisTemplate<String, String> redisTemplate;

    public String generateOtp() {
        int bound = (int) Math.pow(10, OTP_LENGTH);
        int number = SECURE_RANDOM.nextInt(bound);
        return String.format("%0" + OTP_LENGTH + "d", number);
    }

    public void store(String id, String otp, String purpose) {
        final var cacheKey = getCacheKey(id, purpose);

        redisTemplate.opsForValue().set(
                cacheKey,
                DigestUtils.sha256Hex(otp),
                Duration.ofMinutes(5)
        );
    }

    public boolean isOtpValid(String id, String otp, String purpose) {
        final var cacheKey = getCacheKey(id, purpose);

        String storedHash = redisTemplate.opsForValue().get(cacheKey);

        if (storedHash == null) {
            return false;
        }

        String providedHash = DigestUtils.sha256Hex(otp);

        return storedHash.equals(providedHash);
    }

    public void deleteOtp(String id, String purpose) {
        redisTemplate.delete(getCacheKey(id, purpose));
    }

    private String getCacheKey(String id, String purpose) {
        return "otp:%s:%s".formatted(purpose, id);
    }
}
