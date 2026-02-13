package com.elec_business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class OtpService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RedisTemplate<String, String> redisTemplate;

    public void store( String otp,String id) {
        final var cacheKey = getCacheKey(id);

        redisTemplate.opsForValue().set(
                cacheKey, otp, Duration.ofMinutes(5));
    }

    public boolean isOtpValid(final String id, final String otp) {
        final var cacheKey = getCacheKey(id);
        return Objects.equals(
                redisTemplate.opsForValue().get(cacheKey), otp);
    }

    public void deleteOtp(final String id) {
        final var cacheKey = getCacheKey(id);
        redisTemplate.delete(cacheKey);
    }

    private String getCacheKey(String id) {
        return "otp:%s".formatted(id);
    }

    public String generateOtp() {
        String characters = "ABCDEFG123456789";
        int length = 10;
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(characters.length());
            otp.append(characters.charAt(index));
        }
        return otp.toString();
    }
}