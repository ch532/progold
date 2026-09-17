package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AgentNegotiationOrchestrationTest {

    @Test
    public void agentEvaluatesUnknownCapabilitiesAndActions()
            throws Exception {

        AgentConfig config =
                new AgentConfig.Builder(
                        "https://example.com/agent"
                )
                        .apiKey("test-key")
                        .build();

        Agent agent =
                new Agent(config);

        AgentNegotiationRequest request =
                new AgentNegotiationRequest(
                        Arrays.asList(
                                "unknown_capability"
                        ),
                        Arrays.asList(
                                "unknown_action"
                        )
                );

        JSONObject evaluation =
                agent.negotiateRemoteCapabilities(
                        request
                );

        assertNotNull(evaluation);

        JSONArray capabilities =
                evaluation.getJSONArray(
                        "capabilities"
                );

        JSONArray actions =
                evaluation.getJSONArray(
                        "actions"
                );

        assertEquals(
                1,
                capabilities.length()
        );

        assertEquals(
                1,
                actions.length()
        );

        JSONObject capability =
                capabilities.getJSONObject(0);

        assertEquals(
                "unknown_capability",
                capability.getString(
                        "capabilityName"
                )
        );

        assertEquals(
                "DISABLED",
                capability.getString(
                        "state"
                )
        );

        JSONObject action =
                actions.getJSONObject(0);

        assertEquals(
                "unknown_action",
                action.getString(
                        "action"
                )
        );

        assertEquals(
                "NOT_CONFIGURED",
                action.getString(
                        "authorization"
                )
        );
    }

    @Test
    public void emptyNegotiationProducesEmptyEvaluation()
            throws Exception {

        AgentConfig config =
                new AgentConfig.Builder(
                        "https://example.com/agent"
                )
                        .build();

        Agent agent =
                new Agent(config);

        AgentNegotiationRequest request =
                new AgentNegotiationRequest(
                        Collections.emptyList(),
                        Collections.emptyList()
                );

        JSONObject evaluation =
                agent.negotiateRemoteCapabilities(
                        request
                );

        assertNotNull(evaluation);

        assertEquals(
                0,
                evaluation.getJSONArray(
                        "capabilities"
                ).length()
        );

        assertEquals(
                0,
                evaluation.getJSONArray(
                        "actions"
                ).length()
        );
    }

    @Test
    public void negotiationResponseWrapsAgentEvaluation()
            throws Exception {

        AgentConfig config =
                new AgentConfig.Builder(
                        "https://example.com/agent"
                )
                        .build();

        Agent agent =
                new Agent(config);

        AgentNegotiationRequest request =
                new AgentNegotiationRequest(
                        Arrays.asList(
                                "unknown_capability"
                        ),
                        Arrays.asList(
                                "unknown_action"
                        )
                );

        JSONObject evaluation =
                agent.negotiateRemoteCapabilities(
                        request
                );

        AgentNegotiationResponse response =
                new AgentNegotiationResponse(
                        request,
                        evaluation
                );

        JSONObject json =
                response.toJson();

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

        assertTrue(
                json.getJSONObject("evaluation")
                        .has("capabilities")
        );

        assertTrue(
                json.getJSONObject("evaluation")
                        .has("actions")
        );
    }
}
