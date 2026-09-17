package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AgentNegotiationProtocolTest {

    @Test
    public void responseParsesNegotiationRequest() throws Exception {

        JSONObject response = new JSONObject();

        response.put("protocol", "progold");
        response.put("version", "1.0");
        response.put("requestId", "request-001");
        response.put("sessionId", "session-001");
        response.put("status", "requires_negotiation");
        response.put("message", "Additional capabilities requested");

        JSONObject negotiation = new JSONObject();

        JSONArray capabilities = new JSONArray();
        capabilities.put("device_context");
        capabilities.put("location");

        JSONArray actions = new JSONArray();
        actions.put("get_device_info");
        actions.put("get_location");

        negotiation.put("capabilities", capabilities);
        negotiation.put("actions", actions);

        response.put("negotiation", negotiation);

        AgentResponseParser.ParsedResponse parsed =
                AgentResponseParser.parse(
                        response.toString()
                );

        assertEquals(
                "progold",
                parsed.getProtocol()
        );

        assertEquals(
                "1.0",
                parsed.getVersion()
        );

        assertEquals(
                "request-001",
                parsed.getRequestId()
        );

        assertEquals(
                "session-001",
                parsed.getSessionId()
        );

        assertTrue(
                parsed.requiresNegotiation()
        );

        AgentNegotiationRequest request =
                parsed.getNegotiation();

        assertNotNull(request);

        assertEquals(
                Arrays.asList(
                        "device_context",
                        "location"
                ),
                request.getCapabilities()
        );

        assertEquals(
                Arrays.asList(
                        "get_device_info",
                        "get_location"
                ),
                request.getActions()
        );
    }

    @Test
    public void negotiationRequestSerializesCorrectly() throws Exception {

        AgentNegotiationRequest request =
                new AgentNegotiationRequest(
                        Arrays.asList(
                                "device_context",
                                "location"
                        ),
                        Arrays.asList(
                                "get_device_info"
                        )
                );

        JSONObject json =
                request.toJson();

        assertEquals(
                2,
                json.getJSONArray("capabilities").length()
        );

        assertEquals(
                1,
                json.getJSONArray("actions").length()
        );

        assertEquals(
                "device_context",
                json.getJSONArray("capabilities").getString(0)
        );

        assertEquals(
                "get_device_info",
                json.getJSONArray("actions").getString(0)
        );

        assertFalse(
                request.isEmpty()
        );
    }

    @Test
    public void emptyNegotiationRequestIsRecognized() {

        AgentNegotiationRequest request =
                new AgentNegotiationRequest(
                        Collections.emptyList(),
                        Collections.emptyList()
                );

        assertTrue(
                request.isEmpty()
        );

        assertEquals(
                0,
                request.getCapabilities().size()
        );

        assertEquals(
                0,
                request.getActions().size()
        );
    }

    @Test
    public void negotiationResponseSerializesEvaluation() throws Exception {

        AgentNegotiationRequest request =
                new AgentNegotiationRequest(
                        Collections.singletonList(
                                "device_context"
                        ),
                        Collections.singletonList(
                                "get_device_info"
                        )
                );

        JSONObject evaluation =
                new JSONObject();

        JSONArray capabilities =
                new JSONArray();

        JSONObject capability =
                new JSONObject();

        capability.put(
                "capabilityName",
                "device_context"
        );

        capability.put(
                "state",
                "READY"
        );

        capability.put(
                "missingPermissions",
                new JSONArray()
        );

        capabilities.put(capability);

        evaluation.put(
                "capabilities",
                capabilities
        );

        JSONArray actions =
                new JSONArray();

        JSONObject action =
                new JSONObject();

        action.put(
                "action",
                "get_device_info"
        );

        action.put(
                "authorization",
                "AUTHORIZED"
        );

        action.put(
                "permissionsGranted",
                true
        );

        action.put(
                "missingPermissions",
                new JSONArray()
        );

        actions.put(action);

        evaluation.put(
                "actions",
                actions
        );

        AgentNegotiationResponse negotiationResponse =
                new AgentNegotiationResponse(
                        request,
                        evaluation
                );

        JSONObject json =
                negotiationResponse.toJson();

        assertEquals(
                "capability_negotiation",
                json.getString("type")
        );

        assertEquals(
                "evaluated",
                json.getString("status")
        );

        assertNotNull(
                json.getJSONObject("request")
        );

        assertNotNull(
                json.getJSONObject("evaluation")
        );

        assertEquals(
                "READY",
                json.getJSONObject("evaluation")
                        .getJSONArray("capabilities")
                        .getJSONObject(0)
                        .getString("state")
        );
    }
}
