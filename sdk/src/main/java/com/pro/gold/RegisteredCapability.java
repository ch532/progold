package com.pro.gold;

public final class RegisteredCapability {

    private final AgentCapability capability;
    private final ActionSchema actionSchema;
    private final AgentCapabilityHandler handler;

    public RegisteredCapability(
            AgentCapability capability,
            ActionSchema actionSchema,
            AgentCapabilityHandler handler
    ) {

        if (capability == null) {
            throw new IllegalArgumentException(
                    "Capability cannot be null"
            );
        }

        if (actionSchema == null) {
            throw new IllegalArgumentException(
                    "Action schema cannot be null"
            );
        }

        if (handler == null) {
            throw new IllegalArgumentException(
                    "Capability handler cannot be null"
            );
        }

        this.capability = capability;
        this.actionSchema = actionSchema;
        this.handler = handler;
    }

    public AgentCapability getCapability() {
        return capability;
    }

    public ActionSchema getActionSchema() {
        return actionSchema;
    }

    public AgentCapabilityHandler getHandler() {
        return handler;
    }
}
