package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class ProtocolAuditTest {

    @Test
    public void dumpProtocolJson() throws Exception {

        System.out.println("\n========== PROGOLD PROTOCOL AUDIT ==========\n");

        AgentRequest request =
                new AgentRequest(
                        "request-001",
                        "session-001",
                        "Get device information",
                        new JSONObject()
                                .put("source", "consumer-app"),
                        new JSONArray()
                                .put("device")
                                .put("network")
                );

        print("AgentRequest", request.toJson());

        AgentAction action =
                new AgentAction(
                        "action-001",
                        "get_device_info",
                        new JSONObject()
                                .put("includeBattery", true)
                );

        print("AgentAction", action.toJson());

        AgentActionResult success =
                AgentActionResult.success(
                        "action-001",
                        new JSONObject()
                                .put("model", "Test Device")
                                .put("sdk", 36)
                );

        print("AgentActionResult.success", success.toJson());

        AgentActionResult failure =
                AgentActionResult.failure(
                        "action-002",
                        "Permission denied"
                );

        print("AgentActionResult.failure", failure.toJson());

        AgentCapability capability =
                new AgentCapability(
                        "device",
                        "Device information capability"
                );

        print("AgentCapability", capability.toJson());

        CapabilityRegistry capabilityRegistry =
                new CapabilityRegistry();

        capabilityRegistry.register(capability);

        AgentCapabilityManifest manifest =
                new AgentCapabilityManifest(
                        capabilityRegistry
                );

        print("AgentCapabilityManifest", manifest.toJson());

        AgentNegotiationRequest negotiationRequest =
                new AgentNegotiationRequest(
                        Arrays.asList(
                                "device",
                                "network"
                        ),
                        Arrays.asList(
                                "get_device_info",
                                "get_network_info"
                        )
                );

        print(
                "AgentNegotiationRequest",
                negotiationRequest.toJson()
        );

        AgentNegotiationResponse negotiationResponse =
                new AgentNegotiationResponse(
                        negotiationRequest,
                        new JSONObject()
                                .put("device", "ready")
                                .put("network", "ready")
                );

        print(
                "AgentNegotiationResponse",
                negotiationResponse.toJson()
        );

        AgentMessage userMessage =
                new AgentMessage(
                        AgentMessage.Role.USER,
                        "Get my device information"
                );

        print(
                "AgentMessage",
                userMessage.toJson()
        );

        AgentConversation conversation =
                new AgentConversation(
                        "session-001"
                );

        conversation.addMessage(userMessage);

        conversation.addAgentMessage(
                "I will inspect the device."
        );

        print(
                "AgentConversation",
                conversation.toJsonObject()
        );

        AgentTask task =
                new AgentTask(
                        "session-001",
                        "Get device information"
                );

        task.start();
        task.addAction(action);
        task.addActionResult(success);

        print(
                "AgentTask",
                task.toJson()
        );

        CapabilityNegotiationResult negotiationResult =
                new CapabilityNegotiationResult(
                        "device",
                        CapabilityState.READY,
                        Collections.emptyList(),
                        "Capability is ready"
                );

        print(
                "CapabilityNegotiationResult",
                negotiationResult.toJson()
        );

        AgentPermissionRequirement permissionRequirement =
                new AgentPermissionRequirement(
                        "get_location",
                        Arrays.asList(
                                new AgentPermission(
                                        "android.permission.ACCESS_FINE_LOCATION",
                                        true
                                )
                        )
                );

        print(
                "AgentPermissionRequirement",
                new JSONObject()
                        .put(
                                "actionName",
                                permissionRequirement.getActionName()
                        )
                        .put(
                                "permissions",
                                permissionRequirement.toJson()
                        )
        );

        AgentPermissionResult permissionResult =
                AgentPermissionResult.denied(
                        Collections.singletonList(
                                "android.permission.ACCESS_FINE_LOCATION"
                        )
                );

        print(
                "AgentPermissionResult",
                permissionResult.toJson()
        );

        System.out.println(
                "\n========== END PROTOCOL AUDIT ==========\n"
        );

        assertNotNull(request.toJson());
        assertNotNull(action.toJson());
        assertNotNull(success.toJson());
        assertNotNull(negotiationRequest.toJson());
    }

    private void print(
            String name,
            JSONObject json
    ) throws Exception {

        System.out.println(
                "\n--- " + name + " ---"
        );

        System.out.println(
                json.toString(2)
        );
    }
}
