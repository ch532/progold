package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parses responses from a Progold-compatible agent.
 */
public final class AgentResponseParser {

    private AgentResponseParser() {
    }

    public static ParsedResponse parse(
            String body
    ) {

        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Agent response is empty"
            );
        }

        try {
            JSONObject json =
                    new JSONObject(body);

            String protocol =
                    json.optString(
                            "protocol",
                            ""
                    );

            String version =
                    json.optString(
                            "version",
                            ""
                    );

            String requestId =
                    json.optString(
                            "requestId",
                            ""
                    );

            String sessionId =
                    json.optString(
                            "sessionId",
                            null
                    );

            String status =
                    json.optString(
                            "status",
                            "unknown"
                    );

            String message =
                    json.optString(
                            "message",
                            null
                    );

            JSONObject result =
                    json.optJSONObject(
                            "result"
                    );

            List<AgentAction> actions =
                    parseActions(
                            json.optJSONArray(
                                    "actions"
                            )
                    );

            AgentNegotiationRequest negotiation =
                    parseNegotiation(
                            json.optJSONObject(
                                    "negotiation"
                            )
                    );

            return new ParsedResponse(
                    protocol,
                    version,
                    requestId,
                    sessionId,
                    status,
                    message,
                    result,
                    actions,
                    negotiation
            );

        } catch (Exception exception) {

            throw new IllegalArgumentException(
                    "Invalid Progold agent response",
                    exception
            );
        }
    }

    private static List<AgentAction> parseActions(
            JSONArray array
    ) {

        if (array == null) {
            return Collections.emptyList();
        }

        List<AgentAction> actions =
                new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {

            JSONObject action =
                    array.optJSONObject(i);

            if (action == null) {
                continue;
            }

            String id =
                    action.optString(
                            "id",
                            ""
                    );

            String name =
                    action.optString(
                            "name",
                            ""
                    );

            JSONObject parameters =
                    action.optJSONObject(
                            "parameters"
                    );

            if (parameters == null) {
                parameters =
                        new JSONObject();
            }

            if (!id.isEmpty()
                    && !name.isEmpty()) {

                actions.add(
                        new AgentAction(
                                id,
                                name,
                                parameters
                        )
                );
            }
        }

        return actions;
    }

    private static AgentNegotiationRequest parseNegotiation(
            JSONObject negotiation
    ) {

        if (negotiation == null) {
            return new AgentNegotiationRequest(
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }

        List<String> capabilities =
                parseStringArray(
                        negotiation.optJSONArray(
                                "capabilities"
                        )
                );

        List<String> actions =
                parseStringArray(
                        negotiation.optJSONArray(
                                "actions"
                        )
                );

        return new AgentNegotiationRequest(
                capabilities,
                actions
        );
    }

    private static List<String> parseStringArray(
            JSONArray array
    ) {

        if (array == null) {
            return Collections.emptyList();
        }

        List<String> values =
                new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {

            String value =
                    array.optString(
                            i,
                            ""
                    );

            if (!value.isEmpty()) {
                values.add(value);
            }
        }

        return values;
    }

    public static final class ParsedResponse {

        private final String protocol;
        private final String version;
        private final String requestId;
        private final String sessionId;
        private final String status;
        private final String message;
        private final JSONObject result;
        private final List<AgentAction> actions;
        private final AgentNegotiationRequest negotiation;

        private ParsedResponse(
                String protocol,
                String version,
                String requestId,
                String sessionId,
                String status,
                String message,
                JSONObject result,
                List<AgentAction> actions,
                AgentNegotiationRequest negotiation
        ) {

            this.protocol = protocol;
            this.version = version;
            this.requestId = requestId;
            this.sessionId = sessionId;
            this.status = status;
            this.message = message;
            this.result = result;
            this.actions = actions;
            this.negotiation = negotiation;
        }

        public String getProtocol() {
            return protocol;
        }

        public String getVersion() {
            return version;
        }

        public String getRequestId() {
            return requestId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public JSONObject getResult() {
            return result;
        }

        public List<AgentAction> getActions() {
            return actions;
        }

        public AgentNegotiationRequest getNegotiation() {
            return negotiation;
        }

        public boolean requiresAction() {
            return !actions.isEmpty();
        }

        public boolean requiresNegotiation() {
            return negotiation != null
                    && !negotiation.isEmpty();
        }
    }
}
