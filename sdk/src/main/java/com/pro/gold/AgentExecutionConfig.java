package com.pro.gold;

public final class AgentExecutionConfig {

    private final int maxTurns;

    public AgentExecutionConfig() {
        this(10);
    }

    public AgentExecutionConfig(
            int maxTurns
    ) {

        if (maxTurns <= 0) {
            throw new IllegalArgumentException(
                    "Maximum turns must be greater than zero"
            );
        }

        this.maxTurns = maxTurns;
    }

    public int getMaxTurns() {
        return maxTurns;
    }
}
