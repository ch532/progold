package com.pro.gold;

public final class AgentAuthorization {

    public enum Status {
        AUTHORIZED,
        DENIED,
        NOT_CONFIGURED
    }

    private final String actionName;
    private volatile Status status;

    public AgentAuthorization(
            String actionName
    ) {

        if (actionName == null ||
                actionName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Action name cannot be empty"
            );
        }

        this.actionName = actionName;
        this.status = Status.NOT_CONFIGURED;
    }

    public String getActionName() {
        return actionName;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isAuthorized() {
        return status == Status.AUTHORIZED;
    }

    public void authorize() {
        status = Status.AUTHORIZED;
    }

    public void deny() {
        status = Status.DENIED;
    }

    public void reset() {
        status = Status.NOT_CONFIGURED;
    }
}
