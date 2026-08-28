package com.dero.opcg_api.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;
    private record Attempt(int failedCount, Instant lockedUntil) {}
    private final ConcurrentHashMap<String, Attempt> attemptsByEmail = new ConcurrentHashMap<>();

    public void loginFailed(String email) {
        String key = normalize(email);

        attemptsByEmail.compute(key, (k, current) -> {
            int newCount = (current == null ? 0 : current.failedCount()) + 1;
            Instant lockedUntil = newCount >= MAX_ATTEMPTS
                    ? Instant.now().plusSeconds(LOCK_DURATION_MINUTES * 60)
                    : null;
            return new Attempt(newCount, lockedUntil);
        });

    }

    public void loginSucceeded(String email) {
        attemptsByEmail.remove(normalize(email));
    }

    public boolean isLocked(String email) {
        Attempt attempt = attemptsByEmail.get(normalize(email));

        if (attempt == null || attempt.lockedUntil() == null) {
            return false;
        }

        if (Instant.now().isAfter(attempt.lockedUntil())) {
            attemptsByEmail.remove(normalize(email));
            return false;
        }

        return true;
    }

    public String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
