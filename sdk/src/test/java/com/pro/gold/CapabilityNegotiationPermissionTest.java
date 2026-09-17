package com.pro.gold;

import org.json.JSONObject;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CapabilityNegotiationPermissionTest {

    @Test
    public void authorizedCapabilityWithRequiredPermissionNeedsPermission()
            throws Exception {

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
                        "location",
                        "Provides device location",
                        "1.0",
                        true,
                        new JSONObject()
                );

        ActionSchema schema =
                new ActionSchema(
                        "get_location",
                        "Gets device location",
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
                "get_location"
        );

        authorizationRegistry.authorize(
                "get_location"
        );

        AgentPermission locationPermission =
                new AgentPermission(
                        "android.permission.ACCESS_FINE_LOCATION",
                        true
                );

        permissionRegistry.register(
                "get_location",
                locationPermission
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
                        "location"
                );

        assertNotNull(result);

        assertEquals(
                "location",
                result.getCapabilityName()
        );

        assertEquals(
                CapabilityState.PERMISSION_REQUIRED,
                result.getState()
        );

        assertFalse(
                result.isReady()
        );

        assertEquals(
                1,
                result.getMissingPermissions().size()
        );

        assertEquals(
                "android.permission.ACCESS_FINE_LOCATION",
                result.getMissingPermissions().get(0)
        );

        assertEquals(
                "Android permission state requires a Context",
                result.getReason()
        );
    }

    @Test
    public void permissionRequirementIsRegisteredCorrectly()
            throws Exception {

        PermissionRegistry registry =
                new PermissionRegistry();

        AgentPermission permission =
                new AgentPermission(
                        "android.permission.ACCESS_FINE_LOCATION",
                        true
                );

        registry.register(
                "get_location",
                permission
        );

        assertTrue(
                registry.contains(
                        "get_location"
                )
        );

        assertEquals(
                1,
                registry.size()
        );

        AgentPermissionRequirement requirement =
                registry.get(
                        "get_location"
                );

        assertNotNull(requirement);

        assertEquals(
                "get_location",
                requirement.getActionName()
        );

        assertEquals(
                1,
                requirement.getPermissions().size()
        );

        assertEquals(
                "android.permission.ACCESS_FINE_LOCATION",
                requirement.getPermissions()
                        .get(0)
                        .getPermission()
        );

        assertTrue(
                requirement.getPermissions()
                        .get(0)
                        .isRequired()
        );
    }

    @Test
    public void permissionRequiredResultSerializesCorrectly()
            throws Exception {

        CapabilityNegotiationResult result =
                new CapabilityNegotiationResult(
                        "location",
                        CapabilityState.PERMISSION_REQUIRED,
                        Collections.singletonList(
                                "android.permission.ACCESS_FINE_LOCATION"
                        ),
                        "Required permission has not been granted"
                );

        JSONObject json =
                result.toJson();

        assertEquals(
                "location",
                json.getString(
                        "capabilityName"
                )
        );

        assertEquals(
                "PERMISSION_REQUIRED",
                json.getString(
                        "state"
                )
        );

        assertEquals(
                1,
                json.getJSONArray(
                        "missingPermissions"
                ).length()
        );

        assertEquals(
                "android.permission.ACCESS_FINE_LOCATION",
                json.getJSONArray(
                        "missingPermissions"
                ).getString(0)
        );

        assertEquals(
                "Required permission has not been granted",
                json.getString("reason")
        );
    }
}
