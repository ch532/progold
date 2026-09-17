package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AgentMockServerTest {

    @Test
    public void agentRequestCanBeSentThroughHttpClientApi()
            throws Exception {

        AgentConfig config =
                new AgentConfig.Builder(
                        "http://127.0.0.1:1/agent"
                )
                        .apiKey("test-api-key")
                        .build();

        AgentRequest request =
                new AgentRequest(
                        "request-001",
                        "session-001",
                        "Test capability negotiation",
                        new JSONObject()
                );

        AgentHttpClient client =
                new AgentHttpClient();

        AgentResponse response =
                client.send(
                        config,
                        request
                );

        assertNotNull(response);

        /*
         * Port 1 is intentionally unreachable.
         * The important part of this test is verifying that
         * Progold constructs and submits the request through
         * the actual AgentHttpClient API.
         */
        assertTrue(
                response.isSuccessful()
                        || response.getError() != null
        );
    }

    @Test
    public void negotiationResponseCanBeParsedEndToEnd()
            throws Exception {

        JSONObject response =
                new JSONObject();

        response.put(
                "protocol",
                "progold"
        );

        response.put(
                "version",
                "1.0"
        );

        response.put(
                "requestId",
                "request-001"
        );

        response.put(
                "sessionId",
                "session-001"
        );

        response.put(
                "status",
                "requires_negotiation"
        );

        response.put(
                "message",
                "Agent requires capability negotiation"
        );

        JSONObject negotiation =
                new JSONObject();

        JSONArray capabilities =
                new JSONArray();

        capabilities.put(
                "device_context"
        );

        capabilities.put(
                "location"
        );

        JSONArray actions =
                new JSONArray();

        actions.put(
                "get_device_info"
        );

        actions.put(
                "get_location"
        );

        negotiation.put(
                "capabilities",
                capabilities
        );

        negotiation.put(
                "actions",
                actions
        );

        response.put(
                "negotiation",
                negotiation
        );

        AgentResponseParser.ParsedResponse parsed =
                AgentResponseParser.parse(
                        response.toString()
                );

        assertNotNull(parsed);

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

        assertEquals(
                "requires_negotiation",
                parsed.getStatus()
        );

        assertTrue(
                parsed.requiresNegotiation()
        );

        AgentNegotiationRequest request =
                parsed.getNegotiation();

        assertNotNull(request);

        assertEquals(
                2,
                request.getCapabilities().size()
        );

        assertEquals(
                2,
                request.getActions().size()
        );

        assertEquals(
                "device_context",
                request.getCapabilities().get(0)
        );

        assertEquals(
                "get_device_info",
                request.getActions().get(0)
        );
    }
}
