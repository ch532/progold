package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AgentConversation {

    private final String sessionId;
    private final List<AgentMessage> messages =
            new ArrayList<>();

    public AgentConversation(String sessionId) {

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

    public synchronized void addMessage(
            AgentMessage message
    ) {

        if (message == null) {
            throw new IllegalArgumentException(
                    "Message cannot be null"
            );
        }

        messages.add(message);
    }

    public synchronized AgentMessage addUserMessage(
            String content
    ) {

        AgentMessage message =
                new AgentMessage(
                        AgentMessage.Role.USER,
                        content
                );

        messages.add(message);

        return message;
    }

    public synchronized AgentMessage addAgentMessage(
            String content
    ) {

        AgentMessage message =
                new AgentMessage(
                        AgentMessage.Role.AGENT,
                        content
                );

        messages.add(message);

        return message;
    }

    public synchronized AgentMessage addSystemMessage(
            String content
    ) {

        AgentMessage message =
                new AgentMessage(
                        AgentMessage.Role.SYSTEM,
                        content
                );

        messages.add(message);

        return message;
    }

    public synchronized List<AgentMessage> getMessages() {

        return Collections.unmodifiableList(
                new ArrayList<>(messages)
        );
    }

    public synchronized int size() {
        return messages.size();
    }

    public synchronized boolean isEmpty() {
        return messages.isEmpty();
    }

    public synchronized void clear() {
        messages.clear();
    }

    public synchronized JSONArray toJson() {

        JSONArray array =
                new JSONArray();

        for (AgentMessage message : messages) {
            array.put(message.toJson());
        }

        return array;
    }

    public synchronized JSONObject toJsonObject() {

        JSONObject json =
                new JSONObject();

        try {
            json.put(
                    "sessionId",
                    sessionId
            );

            json.put(
                    "messages",
                    toJson()
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to serialize conversation",
                    exception
            );
        }

        return json;
    }
}
