package com.pro.gold;

public final class AgentExecutionResult {

    private final boolean successful;
    private final int turns;
    private final AgentResponse response;
    private final String error;

    private AgentExecutionResult(
            boolean successful,
            int turns,
            AgentResponse response,
            String error
    ) {
        this.successful = successful;
        this.turns = turns;
        this.response = response;
        this.error = error;
    }

    public static AgentExecutionResult success(
            int turns,
            AgentResponse response
    ) {

        return new AgentExecutionResult(
                true,
                turns,
                response,
                null
        );
    }

    public static AgentExecutionResult failure(
            int turns,
            AgentResponse response,
            String error
    ) {

        return new AgentExecutionResult(
                false,
                turns,
                response,
                error
        );
    }

    public boolean isSuccessful() {
        return successful;
    }

    public int getTurns() {
        return turns;
    }

    public AgentResponse getResponse() {
        return response;
    }

    public String getError() {
        return error;
    }
}
