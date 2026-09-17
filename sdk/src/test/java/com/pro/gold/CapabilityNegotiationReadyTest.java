package com.pro.gold;

import org.json.JSONObject;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CapabilityNegotiationReadyTest {

    @Test
    public void authorizedCapabilityWithoutPermissionsIsReady()
            throws Exception {

        CapabilityRegistry capabilityRegistry =
                new CapabilityRegistry();

        CapabilityActionRegistry actionRegistry =
                new CapabilityActionRegistry();

        AuthorizationRegistry authorizationRegistry =
                new AuthorizationRegistry();

        PermissionRegistry permissionRegistry =
                new PermissionRegistry();

        AgentPermissionChecker permissionChecker = null;

        AgentCapability capability =
                new AgentCapability(
                        "device_context",
                        "Provides device information",
                        "1.0",
                        true,
                        new JSONObject()
                );

        ActionSchema schema =
                new ActionSchema(
                        "get_device_info",
                        "Gets device information",
                        new JSONObject()
                );

        AgentCapabilityHandler handler =
                action -> AgentActionResult.success(
                        action.getId(),
                        new JSONObject()
                );

        capabilityRegistry.register(
                capability
        );

        actionRegistry.register(
                capability,
                schema,
                handler
        );

        authorizationRegistry.register(
                "get_device_info"
        );

        authorizationRegistry.authorize(
                "get_device_info"
        );

        CapabilityNegotiation negotiation =
                new CapabilityNegotiation(
                        capabilityRegistry,
                        actionRegistry,
                        authorizationRegistry,
                        permissionRegistry,
                        permissionChecker
                );

        CapabilityNegotiationResult result =
                negotiation.negotiate(
                        "device_context"
                );

        assertNotNull(result);

        assertEquals(
                "device_context",
                result.getCapabilityName()
        );

        assertEquals(
                CapabilityState.READY,
                result.getState()
        );

        assertTrue(
                result.isReady()
        );

        assertTrue(
                result.getMissingPermissions()
                        .isEmpty()
        );
    }

    @Test
    public void disabledCapabilityIsNotReady() {

        CapabilityRegistry capabilityRegistry =
                new CapabilityRegistry();

        CapabilityActionRegistry actionRegistry =
                new CapabilityActionRegistry();

        AuthorizationRegistry authorizationRegistry =
                new AuthorizationRegistry();

        PermissionRegistry permissionRegistry =
                new PermissionRegistry();

        AgentCapability capability =
                new AgentCapability(
                        "device_context",
                        "Provides device information",
                        "1.0",
                        false,
                        new JSONObject()
                );

        capabilityRegistry.register(
                capability
        );

        CapabilityNegotiation negotiation =
                new CapabilityNegotiation(
                        capabilityRegistry,
                        actionRegistry,
                        authorizationRegistry,
                        permissionRegistry,
                        null
                );

        CapabilityNegotiationResult result =
                negotiation.negotiate(
                        "device_context"
                );

        assertEquals(
                CapabilityState.DISABLED,
                result.getState()
        );

        assertFalse(
                result.isReady()
        );
    }

    @Test
    public void unauthorizedActionPreventsReadyState() {

        CapabilityRegistry capabilityRegistry =
                new CapabilityRegistry();

        CapabilityActionRegistry actionRegistry =
                new CapabilityActionRegistry();

        AuthorizationRegistry authorizationRegistry =
                new AuthorizationRegistry();

        PermissionRegistry permissionRegistry =
                new PermissionRegistry();

        AgentCapability capability =
                new AgentCapability(
                        "device_context",
                        "Provides device information",
                        "1.0",
                        true,
                        new JSONObject()
                );

        ActionSchema schema =
                new ActionSchema(
                        "get_device_info",
                        "Gets device information",
                        new JSONObject()
                );

        AgentCapabilityHandler handler =
                action -> AgentActionResult.success(
                        action.getId(),
                        new JSONObject()
                );

        capabilityRegistry.register(
                capability
        );

        actionRegistry.register(
                capability,
                schema,
                handler
        );

        authorizationRegistry.register(
                "get_device_info"
        );

        CapabilityNegotiation negotiation =
                new CapabilityNegotiation(
                        capabilityRegistry,
                        actionRegistry,
                        authorizationRegistry,
                        permissionRegistry,
                        null
                );

        CapabilityNegotiationResult result =
                negotiation.negotiate(
                        "device_context"
                );

        assertEquals(
                CapabilityState.UNAUTHORIZED,
                result.getState()
        );

        assertFalse(
                result.isReady()
        );
    }

    @Test
    public void negotiationResultSerializesReadyState()
            throws Exception {

        CapabilityNegotiationResult result =
                new CapabilityNegotiationResult(
                        "device_context",
                        CapabilityState.READY,
                        Collections.emptyList(),
                        null
                );

        JSONObject json =
                result.toJson();

        assertEquals(
                "device_context",
                json.getString(
                        "capabilityName"
                )
        );

        assertEquals(
                "READY",
                json.getString(
                        "state"
                )
        );

        assertEquals(
                0,
                json.getJSONArray(
                        "missingPermissions"
                ).length()
        );
    }
}
