package com.pro.gold;

import android.content.Context;

import java.util.List;

public final class AgentActionExecutor {

    private final CapabilityActionRegistry capabilityActionRegistry;
    private final AuthorizationRegistry authorizationRegistry;
    private final PermissionRegistry permissionRegistry;
    private final AgentPermissionChecker permissionChecker;

    public AgentActionExecutor(
            CapabilityActionRegistry capabilityActionRegistry,
            AuthorizationRegistry authorizationRegistry,
            PermissionRegistry permissionRegistry,
            AgentPermissionChecker permissionChecker
    ) {
        if (capabilityActionRegistry == null) {
            throw new IllegalArgumentException(
                    "CapabilityActionRegistry cannot be null"
            );
        }

        if (authorizationRegistry == null) {
            throw new IllegalArgumentException(
                    "AuthorizationRegistry cannot be null"
            );
        }

        if (permissionRegistry == null) {
            throw new IllegalArgumentException(
                    "PermissionRegistry cannot be null"
            );
        }

        this.capabilityActionRegistry =
                capabilityActionRegistry;

        this.authorizationRegistry =
                authorizationRegistry;

        this.permissionRegistry =
                permissionRegistry;

        this.permissionChecker =
                permissionChecker;
    }

    public AgentActionResult execute(
            AgentAction action
    ) {
        return execute(
                action,
                null
        );
    }

    public AgentActionResult execute(
            AgentAction action,
            Context context
    ) {
        if (action == null) {
            throw new IllegalArgumentException(
                    "Agent action cannot be null"
            );
        }

        String actionName =
                action.getName();

        if (actionName == null ||
                actionName.trim().isEmpty()) {

            return AgentActionResult.failure(
                    action.getId(),
                    "Action name cannot be empty"
            );
        }

        RegisteredCapability registered =
                findCapability(actionName);

        if (registered == null) {
            return AgentActionResult.failure(
                    action.getId(),
                    "No capability is registered for action: "
                            + actionName
            );
        }

        AgentAuthorization.Status authorization =
                authorizationRegistry.getStatus(
                        actionName
                );

        if (authorization !=
                AgentAuthorization.Status.AUTHORIZED) {

            return AgentActionResult.failure(
                    action.getId(),
                    "Action is not authorized: "
                            + actionName
            );
        }

        AgentPermissionRequirement requirement =
                permissionRegistry.get(
                        actionName
                );

        if (requirement != null &&
                requirement.requiresPermissions()) {

            AgentPermissionChecker checker =
                    permissionChecker;

            if (checker == null &&
                    context != null) {

                checker =
                        new AgentPermissionChecker(
                                context
                        );
            }

            if (checker == null) {
                return AgentActionResult.failure(
                        action.getId(),
                        "Android permission state requires a Context"
                );
            }

            List<String> missing =
                    checker.getMissingPermissions(
                            requirement.getPermissions()
                    );

            if (!missing.isEmpty()) {
                return AgentActionResult.failure(
                        action.getId(),
                        "Required Android permissions are not granted: "
                                + missing
                );
            }
        }

        try {
            AgentActionResult result =
                    registered
                            .getHandler()
                            .handle(action);

            if (result == null) {
                return AgentActionResult.failure(
                        action.getId(),
                        "Capability handler returned null"
                );
            }

            return result;

        } catch (Exception exception) {
            String message =
                    exception.getMessage();

            if (message == null ||
                    message.trim().isEmpty()) {

                message =
                        exception.getClass()
                                .getSimpleName();
            }

            return AgentActionResult.failure(
                    action.getId(),
                    message
            );
        }
    }

    private RegisteredCapability findCapability(
            String actionName
    ) {
        for (RegisteredCapability registered :
                capabilityActionRegistry
                        .getCapabilities()) {

            if (registered == null) {
                continue;
            }

            ActionSchema schema =
                    registered.getActionSchema();

            if (schema == null) {
                continue;
            }

            if (actionName.equals(
                    schema.getName()
            )) {
                return registered;
            }
        }

        return null;
    }
}
