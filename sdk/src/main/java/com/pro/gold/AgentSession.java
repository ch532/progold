package com.pro.gold;

import java.util.UUID;

/**
 * Represents a Progold agent session.
 */
public final class AgentSession {

    private final String sessionId;

    public AgentSession() {
        this(UUID.randomUUID().toString());
    }

    public AgentSession(String sessionId) {

        if (sessionId == null ||
                sessionId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Session ID cannot be empty"
            );
        }

        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
