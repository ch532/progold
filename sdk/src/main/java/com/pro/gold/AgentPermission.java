package com.pro.gold;

public final class AgentPermission {

    private final String permission;
    private final boolean required;

    public AgentPermission(
            String permission
    ) {
        this(
                permission,
                true
        );
    }

    public AgentPermission(
            String permission,
            boolean required
    ) {

        if (permission == null ||
                permission.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Permission cannot be empty"
            );
        }

        this.permission = permission;
        this.required = required;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isRequired() {
        return required;
    }
}
