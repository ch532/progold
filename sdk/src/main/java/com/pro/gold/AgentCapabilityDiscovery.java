package com.pro.gold;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

public final class AgentCapabilityDiscovery {

    private final CapabilityRegistry capabilityRegistry;
    private final PermissionRegistry permissionRegistry;
    private final AuthorizationRegistry
            authorizationRegistry;
    private final CapabilityNegotiation
            capabilityNegotiation;

    public AgentCapabilityDiscovery(
            CapabilityRegistry capabilityRegistry,
            CapabilityActionRegistry
                    capabilityActionRegistry,
            PermissionRegistry permissionRegistry,
            AuthorizationRegistry authorizationRegistry,
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

        if (permissionRegistry == null) {
            throw new IllegalArgumentException(
                    "PermissionRegistry cannot be null"
            );
        }

        if (authorizationRegistry == null) {
            throw new IllegalArgumentException(
                    "AuthorizationRegistry cannot be null"
            );
        }

        this.capabilityRegistry =
                capabilityRegistry;

        this.permissionRegistry =
                permissionRegistry;

        this.authorizationRegistry =
                authorizationRegistry;

        this.capabilityNegotiation =
                new CapabilityNegotiation(
                        capabilityRegistry,
                        capabilityActionRegistry,
                        authorizationRegistry,
                        permissionRegistry,
                        permissionChecker
                );
    }

    public JSONObject discover(
            Context context
    ) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        JSONObject discovery =
                new JSONObject();

        try {

            discovery.put(
                    "protocol",
                    "progold"
            );

            discovery.put(
                    "version",
                    "1.0"
            );

            discovery.put(
                    "device",
                    AgentDeviceContext
                            .from(context)
                            .toJson()
            );

            discovery.put(
                    "capabilities",
                    capabilityRegistry.toJson()
            );

            discovery.put(
                    "permissions",
                    permissionRegistry.toJson()
            );

            discovery.put(
                    "authorization",
                    authorizationRegistry.toJson()
            );

            discovery.put(
                    "negotiation",
                    negotiateCapabilities(context)
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to create capability discovery document",
                    exception
            );
        }

        return discovery;
    }

    public JSONArray discoverCapabilities() {
        return capabilityRegistry.toJson();
    }

    public JSONArray discoverPermissions() {
        return permissionRegistry.toJson();
    }

    public JSONArray discoverAuthorization() {
        return authorizationRegistry.toJson();
    }

    public JSONArray negotiateCapabilities(
            Context context
    ) {

        JSONArray array =
                new JSONArray();

        for (String capabilityName :
                capabilityRegistry.getNames()) {

            CapabilityNegotiationResult result =
                    capabilityNegotiation.negotiate(
                            capabilityName,
                            context
                    );

            array.put(
                    result.toJson()
            );
        }

        return array;
    }
}
