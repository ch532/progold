package com.pro.gold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages active Progold agent sessions.
 */
public final class AgentSessionManager {

    private final Map<String, AgentSession> sessions =
            new ConcurrentHashMap<>();

    /**
     * Creates and stores a new session.
     */
    public AgentSession create() {

        AgentSession session =
                new AgentSession();

        sessions.put(
                session.getSessionId(),
                session
        );

        return session;
    }

    /**
     * Stores an existing session.
     */
    public void add(
            AgentSession session
    ) {

        if (session == null) {
            throw new IllegalArgumentException(
                    "Session cannot be null"
            );
        }

        sessions.put(
                session.getSessionId(),
                session
        );
    }

    /**
     * Finds a session by ID.
     */
    public AgentSession get(
            String sessionId
    ) {

        if (sessionId == null) {
            return null;
        }

        return sessions.get(sessionId);
    }

    /**
     * Removes a session.
     */
    public AgentSession remove(
            String sessionId
    ) {

        if (sessionId == null) {
            return null;
        }

        return sessions.remove(sessionId);
    }

    /**
     * Checks whether a session exists.
     */
    public boolean contains(
            String sessionId
    ) {

        if (sessionId == null) {
            return false;
        }

        return sessions.containsKey(sessionId);
    }

    /**
     * Returns the number of active sessions.
     */
    public int size() {
        return sessions.size();
    }

    /**
     * Returns a snapshot of active session IDs.
     */
    public List<String> getSessionIds() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        sessions.keySet()
                )
        );
    }

    /**
     * Removes all active sessions.
     */
    public void clear() {
        sessions.clear();
    }
}
