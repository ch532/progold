package com.pro.gold;

import org.json.JSONObject;

import java.util.UUID;

public final class AgentEvent {

    public enum Type {
        TASK_STARTED,
        REQUEST_SENT,
        RESPONSE_RECEIVED,
        ACTION_REQUESTED,
        ACTION_COMPLETED,
        FOLLOW_UP_SENT,
        TASK_COMPLETED,
        TASK_FAILED
    }

    private final String id;
    private final Type type;
    private final String taskId;
    private final long timestamp;
    private final JSONObject data;

    public AgentEvent(
            Type type,
            String taskId
    ) {
        this(
                type,
                taskId,
                new JSONObject()
        );
    }

    public AgentEvent(
            Type type,
            String taskId,
            JSONObject data
    ) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "Event type cannot be null"
            );
        }

        if (taskId == null ||
                taskId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Task ID cannot be empty"
            );
        }

        this.id =
                UUID.randomUUID().toString();

        this.type = type;
        this.taskId = taskId;

        this.timestamp =
                System.currentTimeMillis();

        this.data =
                data == null
                        ? new JSONObject()
                        : data;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getTaskId() {
        return taskId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public JSONObject getData() {
        return data;
    }

    public JSONObject toJson() {

        JSONObject json =
                new JSONObject();

        try {

            json.put(
                    "id",
                    id
            );

            json.put(
                    "type",
                    type.name().toLowerCase()
            );

            json.put(
                    "taskId",
                    taskId
            );

            json.put(
                    "timestamp",
                    timestamp
            );

            json.put(
                    "data",
                    data
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to serialize agent event",
                    exception
            );
        }

        return json;
    }
}
