package com.pro.gold;

import org.json.JSONObject;

/**
 * Result of an action handled by the host application.
 */
public final class AgentActionResult {

    private final String actionId;
    private final boolean successful;
    private final JSONObject result;
    private final String error;

    private AgentActionResult(
            String actionId,
            boolean successful,
            JSONObject result,
            String error
    ) {
        this.actionId = actionId;
        this.successful = successful;
        this.result = result;
        this.error = error;
    }

    public static AgentActionResult success(
            String actionId,
            JSONObject result
    ) {
        return new AgentActionResult(
                actionId,
                true,
                result == null
                        ? new JSONObject()
                        : result,
                null
        );
    }

    public static AgentActionResult failure(
            String actionId,
            String error
    ) {
        return new AgentActionResult(
                actionId,
                false,
                new JSONObject(),
                error
        );
    }

    public String getActionId() {
        return actionId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public JSONObject getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public JSONObject toJson() {

        JSONObject json = new JSONObject();

        try {
            json.put("actionId", actionId);
            json.put(
                    "status",
                    successful
                            ? "completed"
                            : "failed"
            );

            if (successful) {
                json.put("result", result);
            } else {
                json.put("error", error);
            }

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to create action result",
                    exception
            );
        }

        return json;
    }
}
