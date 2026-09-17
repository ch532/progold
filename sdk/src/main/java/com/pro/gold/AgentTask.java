package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class AgentTask {

    public enum Status {
        CREATED,
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    private final String taskId;
    private final String sessionId;
    private final String task;
    private final long createdAt;

    private volatile long completedAt;
    private volatile Status status;

    private final List<AgentAction> actions =
            new ArrayList<>();

    private final List<AgentActionResult> actionResults =
            new ArrayList<>();

    public AgentTask(
            String sessionId,
            String task
    ) {

        if (sessionId == null ||
                sessionId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Session ID cannot be empty"
            );
        }

        if (task == null ||
                task.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Task cannot be empty"
            );
        }

        this.taskId =
                UUID.randomUUID().toString();

        this.sessionId = sessionId;
        this.task = task;
        this.createdAt =
                System.currentTimeMillis();

        this.status = Status.CREATED;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTask() {
        return task;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public Status getStatus() {
        return status;
    }

    public synchronized void start() {
        status = Status.RUNNING;
    }

    public synchronized void complete() {
        status = Status.COMPLETED;
        completedAt =
                System.currentTimeMillis();
    }

    public synchronized void fail() {
        status = Status.FAILED;
        completedAt =
                System.currentTimeMillis();
    }

    public synchronized void cancel() {
        status = Status.CANCELLED;
        completedAt =
                System.currentTimeMillis();
    }

    public synchronized void addAction(
            AgentAction action
    ) {

        if (action == null) {
            throw new IllegalArgumentException(
                    "Action cannot be null"
            );
        }

        actions.add(action);
    }

    public synchronized void addActionResult(
            AgentActionResult result
    ) {

        if (result == null) {
            throw new IllegalArgumentException(
                    "Action result cannot be null"
            );
        }

        actionResults.add(result);
    }

    public synchronized List<AgentAction> getActions() {

        return Collections.unmodifiableList(
                new ArrayList<>(actions)
        );
    }

    public synchronized List<AgentActionResult>
    getActionResults() {

        return Collections.unmodifiableList(
                new ArrayList<>(actionResults)
        );
    }

    public synchronized JSONObject toJson() {

        JSONObject json =
                new JSONObject();

        try {

            json.put("taskId", taskId);
            json.put("sessionId", sessionId);
            json.put("task", task);
            json.put("status",
                    status.name().toLowerCase());
            json.put("createdAt", createdAt);

            if (completedAt > 0) {
                json.put(
                        "completedAt",
                        completedAt
                );
            }

            JSONArray actionArray =
                    new JSONArray();

            for (AgentAction action : actions) {
                actionArray.put(
                        action.toJson()
                );
            }

            JSONArray resultArray =
                    new JSONArray();

            for (AgentActionResult result :
                    actionResults) {

                resultArray.put(
                        result.toJson()
                );
            }

            json.put(
                    "actions",
                    actionArray
            );

            json.put(
                    "actionResults",
                    resultArray
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to serialize agent task",
                    exception
            );
        }

        return json;
    }
}
