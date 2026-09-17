package com.pro.gold;

import org.json.JSONObject;

import java.util.UUID;

public final class AgentMessage {

    public enum Role {
        USER,
        AGENT,
        SYSTEM,
        ACTION,
        ACTION_RESULT
    }

    private final String id;
    private final Role role;
    private final String content;
    private final long timestamp;
    private final JSONObject metadata;

    public AgentMessage(
            Role role,
            String content
    ) {
        this(
                UUID.randomUUID().toString(),
                role,
                content,
                System.currentTimeMillis(),
                new JSONObject()
        );
    }

    public AgentMessage(
            String id,
            Role role,
            String content,
            long timestamp,
            JSONObject metadata
    ) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Message ID cannot be empty"
            );
        }

        if (role == null) {
            throw new IllegalArgumentException(
                    "Message role cannot be null"
            );
        }

        this.id = id;
        this.role = role;
        this.content = content == null ? "" : content;
        this.timestamp = timestamp;
        this.metadata = metadata == null
                ? new JSONObject()
                : metadata;
    }

    public String getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public JSONObject getMetadata() {
        return metadata;
    }

    public JSONObject toJson() {

        JSONObject json = new JSONObject();

        try {
            json.put("id", id);
            json.put("role", role.name().toLowerCase());
            json.put("content", content);
            json.put("timestamp", timestamp);
            json.put("metadata", metadata);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to create Progold message",
                    exception
            );
        }

        return json;
    }
}
