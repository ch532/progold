package com.pro.gold;

import org.json.JSONObject;

public final class AgentNegotiationResponse {

    private final AgentNegotiationRequest request;
    private final JSONObject evaluation;

    public AgentNegotiationResponse(
            AgentNegotiationRequest request,
            JSONObject evaluation
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "request cannot be null"
            );
        }

        if (evaluation == null) {
            throw new IllegalArgumentException(
                    "evaluation cannot be null"
            );
        }

        this.request = request;
        this.evaluation = evaluation;
    }

    public AgentNegotiationRequest getRequest() {
        return request;
    }

    public JSONObject getEvaluation() {
        return evaluation;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put(
                    "type",
                    "capability_negotiation"
            );

            json.put(
                    "status",
                    "evaluated"
            );

            json.put(
                    "request",
                    request.toJson()
            );

            json.put(
                    "evaluation",
                    evaluation
            );

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to serialize negotiation response",
                    exception
            );
        }

        return json;
    }
}
