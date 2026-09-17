package com.pro.gold;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AgentActionExecutorTest {

    private CapabilityActionRegistry createCapabilityRegistry(
            final boolean shouldSucceed
    ) {
        CapabilityActionRegistry registry =
                new CapabilityActionRegistry();

        AgentCapability capability =
                new AgentCapability(
                        "device_info",
                        "Device information",
                        "1.0",
                        true,
                        null
                );

        ActionSchema schema =
                new ActionSchema(
                        "get_device_info",
                        "Gets device information",
                        null
                );

        registry.register(
                capability,
                schema,
                action -> {
                    if (shouldSucceed) {
                        return AgentActionResult.success(
                                action.getId(),
                                null
                        );
                    }

                    return AgentActionResult.failure(
                            action.getId(),
                            "Handler failed"
                    );
                }
        );

        return registry;
    }

    private AgentAction createAction() {
        return new AgentAction(
                "get_device_info"
        );
    }

    @Test
    public void authorizedActionExecutes() {
        CapabilityActionRegistry capabilities =
                createCapabilityRegistry(true);

        AuthorizationRegistry authorization =
                new AuthorizationRegistry();

        PermissionRegistry permissions =
                new PermissionRegistry();

        authorization.authorize(
                "get_device_info"
        );

        AgentActionExecutor executor =
                new AgentActionExecutor(
                        capabilities,
                        authorization,
                        permissions,
                        null
                );

        AgentAction action =
                createAction();

        AgentActionResult result =
                executor.execute(action);

        assertTrue(result.isSuccessful());

        assertEquals(
                action.getId(),
                result.getActionId()
        );
    }

    @Test
    public void unauthorizedActionIsRejected() {
        CapabilityActionRegistry capabilities =
                createCapabilityRegistry(true);

        AuthorizationRegistry authorization =
                new AuthorizationRegistry();

        PermissionRegistry permissions =
                new PermissionRegistry();

        AgentActionExecutor executor =
                new AgentActionExecutor(
                        capabilities,
                        authorization,
                        permissions,
                        null
                );

        AgentAction action =
                createAction();

        AgentActionResult result =
                executor.execute(action);

        assertFalse(result.isSuccessful());

        assertEquals(
                action.getId(),
                result.getActionId()
        );
    }

    @Test
    public void missingCapabilityIsRejected() {
        CapabilityActionRegistry capabilities =
                new CapabilityActionRegistry();

        AuthorizationRegistry authorization =
                new AuthorizationRegistry();

        PermissionRegistry permissions =
                new PermissionRegistry();

        AgentActionExecutor executor =
                new AgentActionExecutor(
                        capabilities,
                        authorization,
                        permissions,
                        null
                );

        AgentAction action =
                new AgentAction(
                        "unknown_action"
                );

        AgentActionResult result =
                executor.execute(action);

        assertFalse(result.isSuccessful());

        assertEquals(
                action.getId(),
                result.getActionId()
        );
    }

    @Test
    public void handlerFailureIsReturned() {
        CapabilityActionRegistry capabilities =
                createCapabilityRegistry(false);

        AuthorizationRegistry authorization =
                new AuthorizationRegistry();

        PermissionRegistry permissions =
                new PermissionRegistry();

        authorization.authorize(
                "get_device_info"
        );

        AgentActionExecutor executor =
                new AgentActionExecutor(
                        capabilities,
                        authorization,
                        permissions,
                        null
                );

        AgentAction action =
                createAction();

        AgentActionResult result =
                executor.execute(action);

        assertFalse(result.isSuccessful());

        assertEquals(
                "Handler failed",
                result.getError()
        );
    }

    @Test
    public void permissionRequirementWithoutContextIsRejected() {
        CapabilityActionRegistry capabilities =
                createCapabilityRegistry(true);

        AuthorizationRegistry authorization =
                new AuthorizationRegistry();

        PermissionRegistry permissions =
                new PermissionRegistry();

        authorization.authorize(
                "get_device_info"
        );

        permissions.register(
                "get_device_info",
                new AgentPermission(
                        "android.permission.ACCESS_FINE_LOCATION",
                        true
                )
        );

        AgentActionExecutor executor =
                new AgentActionExecutor(
                        capabilities,
                        authorization,
                        permissions,
                        null
                );

        AgentAction action =
                createAction();

        AgentActionResult result =
                executor.execute(action);

        assertFalse(result.isSuccessful());

        assertEquals(
                "Android permission state requires a Context",
                result.getError()
        );
    }
}
