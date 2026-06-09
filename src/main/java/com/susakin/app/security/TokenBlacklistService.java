package com.susakin.app.security;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final ConcurrentHashMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklist(String token, long expirationMs) {
        blacklistedTokens.put(token, System.currentTimeMillis() + expirationMs);
    }

    public boolean isBlacklisted(String token) {
        Long expiresAt = blacklistedTokens.get(token);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt < System.currentTimeMillis()) {
            blacklistedTokens.remove(token);
            return false;
        }
        return true;
    }
}
