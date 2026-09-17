package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

public final class AgentRequest {

    private final String requestId;
    private final String sessionId;
    private final String task;
    private final JSONObject context;
    private final JSONArray capabilities;

    public AgentRequest(
            String sessionId,
            String task
    ) {

        this(
                UUID.randomUUID().toString(),
                sessionId,
                task,
                new JSONObject(),
                new JSONArray()
        );
    }

    public AgentRequest(
            String sessionId,
            String task,
            JSONObject context
    ) {

        this(
                UUID.randomUUID().toString(),
                sessionId,
                task,
                context,
                new JSONArray()
        );
    }

    public AgentRequest(
            String sessionId,
            String task,
            JSONObject context,
            JSONArray capabilities
    ) {

        this(
                UUID.randomUUID().toString(),
                sessionId,
                task,
                context,
                capabilities
        );
    }

    public AgentRequest(
            String requestId,
            String sessionId,
            String task,
            JSONObject context
    ) {

        this(
                requestId,
                sessionId,
                task,
                context,
                new JSONArray()
        );
    }

    public AgentRequest(
            String requestId,
            String sessionId,
            String task,
            JSONObject context,
            JSONArray capabilities
    ) {

        if (requestId == null ||
                requestId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Request ID cannot be empty"
            );
        }

        if (task == null ||
                task.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Task cannot be empty"
            );
        }

        this.requestId = requestId;
        this.sessionId = sessionId;
        this.task = task;

        this.context =
                context == null
                        ? new JSONObject()
                        : context;

        this.capabilities =
                capabilities == null
                        ? new JSONArray()
                        : capabilities;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTask() {
        return task;
    }

    public JSONObject getContext() {
        return context;
    }

    public JSONArray getCapabilities() {
        return capabilities;
    }

    public JSONObject toJson() {

        JSONObject json =
                new JSONObject();

        try {

            json.put(
                    "protocol",
                    "progold"
            );

            json.put(
                    "version",
                    "1.0"
            );

            json.put(
                    "requestId",
                    requestId
            );

            if (sessionId != null &&
                    !sessionId.trim().isEmpty()) {

                json.put(
                        "sessionId",
                        sessionId
                );
            }

            json.put(
                    "task",
                    task
            );

            json.put(
                    "context",
                    context
            );

            json.put(
                    "capabilities",
                    capabilities
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to create Progold request",
                    exception
            );
        }

        return json;
    }
}
