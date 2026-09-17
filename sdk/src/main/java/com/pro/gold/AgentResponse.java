package com.pro.gold;

/**
 * Result returned by an agentic AI endpoint.
 */
public final class AgentResponse {

    private final boolean successful;
    private final int statusCode;
    private final String body;
    private final String error;

    private AgentResponse(
            boolean successful,
            int statusCode,
            String body,
            String error
    ) {
        this.successful = successful;
        this.statusCode = statusCode;
        this.body = body;
        this.error = error;
    }

    public static AgentResponse success(int statusCode, String body) {
        return new AgentResponse(
                true,
                statusCode,
                body,
                null
        );
    }

    public static AgentResponse failure(
            int statusCode,
            String error
    ) {
        return new AgentResponse(
                false,
                statusCode,
                null,
                error
        );
    }

    public boolean isSuccessful() {
        return successful;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public String getError() {
        return error;
    }
}
