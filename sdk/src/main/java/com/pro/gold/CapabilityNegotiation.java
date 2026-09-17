package com.pro.gold;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CapabilityNegotiation {

    private final CapabilityRegistry capabilityRegistry;
    private final CapabilityActionRegistry
            capabilityActionRegistry;
    private final AuthorizationRegistry
            authorizationRegistry;
    private final PermissionRegistry
            permissionRegistry;
    private final AgentPermissionChecker
            permissionChecker;

    public CapabilityNegotiation(
            CapabilityRegistry capabilityRegistry,
            CapabilityActionRegistry capabilityActionRegistry,
            AuthorizationRegistry authorizationRegistry,
            PermissionRegistry permissionRegistry,
            AgentPermissionChecker permissionChecker
    ) {

        if (capabilityRegistry == null) {
            throw new IllegalArgumentException(
                    "CapabilityRegistry cannot be null"
            );
        }

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

        this.capabilityRegistry =
                capabilityRegistry;

        this.capabilityActionRegistry =
                capabilityActionRegistry;

        this.authorizationRegistry =
                authorizationRegistry;

        this.permissionRegistry =
                permissionRegistry;

        this.permissionChecker =
                permissionChecker;
    }

    public CapabilityNegotiationResult negotiate(
            String capabilityName
    ) {

        return negotiate(
                capabilityName,
                null
        );
    }

    public CapabilityNegotiationResult negotiate(
            String capabilityName,
            Context context
    ) {

        AgentCapability capability =
                capabilityRegistry.get(
                        capabilityName
                );

        if (capability == null) {

            return createResult(
                    capabilityName,
                    CapabilityState.DISABLED,
                    Collections.emptyList(),
                    "Capability is not registered"
            );
        }

        if (!capability.isEnabled()) {

            return createResult(
                    capabilityName,
                    CapabilityState.DISABLED,
                    Collections.emptyList(),
                    "Capability is disabled"
            );
        }

        RegisteredCapability registered =
                capabilityActionRegistry.get(
                        capabilityName
                );

        if (registered == null) {

            return createResult(
                    capabilityName,
                    CapabilityState.AVAILABLE,
                    Collections.emptyList(),
                    "Capability has no action binding"
            );
        }

        String actionName =
                registered
                        .getActionSchema()
                        .getName();

        AgentAuthorization.Status status =
                authorizationRegistry.getStatus(
                        actionName
                );

        if (status !=
                AgentAuthorization.Status.AUTHORIZED) {

            return createResult(
                    capabilityName,
                    CapabilityState.UNAUTHORIZED,
                    Collections.emptyList(),
                    "Capability action is not authorized"
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

                return createResult(
                        capabilityName,
                        CapabilityState.PERMISSION_REQUIRED,
                        requiredPermissions(
                                requirement
                        ),
                        "Android permission state requires a Context"
                );
            }

            List<String> missing =
                    checker.getMissingPermissions(
                            requirement
                                    .getPermissions()
                    );

            if (!missing.isEmpty()) {

                return createResult(
                        capabilityName,
                        CapabilityState.PERMISSION_REQUIRED,
                        missing,
                        "Required Android permissions are not granted"
                );
            }
        }

        return createResult(
                capabilityName,
                CapabilityState.READY,
                Collections.emptyList(),
                "Capability is ready"
        );
    }

    private CapabilityNegotiationResult createResult(
            String capabilityName,
            CapabilityState state,
            List<String> missingPermissions,
            String reason
    ) {

        if (capabilityName == null ||
                capabilityName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Capability name cannot be empty"
            );
        }

        return new CapabilityNegotiationResult(
                capabilityName,
                state,
                missingPermissions,
                reason
        );
    }

    private List<String> requiredPermissions(
            AgentPermissionRequirement requirement
    ) {

        List<String> permissions =
                new ArrayList<>();

        for (AgentPermission permission :
                requirement.getPermissions()) {

            if (permission != null &&
                    permission.isRequired()) {

                permissions.add(
                        permission.getPermission()
                );
            }
        }

        return Collections.unmodifiableList(
                permissions
        );
    }
}
