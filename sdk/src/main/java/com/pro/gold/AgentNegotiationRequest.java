package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AgentNegotiationRequest {

    private final List<String> capabilities;
    private final List<String> actions;

    public AgentNegotiationRequest(
            List<String> capabilities,
            List<String> actions
    ) {
        this.capabilities =
                capabilities == null
                        ? Collections.emptyList()
                        : Collections.unmodifiableList(
                                new ArrayList<>(capabilities)
                        );

        this.actions =
                actions == null
                        ? Collections.emptyList()
                        : Collections.unmodifiableList(
                                new ArrayList<>(actions)
                        );
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public List<String> getActions() {
        return actions;
    }

    public boolean isEmpty() {
        return capabilities.isEmpty()
                && actions.isEmpty();
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            JSONArray capabilityArray = new JSONArray();

            for (String capability : capabilities) {
                capabilityArray.put(capability);
            }

            JSONArray actionArray = new JSONArray();

            for (String action : actions) {
                actionArray.put(action);
            }

            json.put(
                    "capabilities",
                    capabilityArray
            );

            json.put(
                    "actions",
                    actionArray
            );

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to serialize negotiation request",
                    exception
            );
        }

        return json;
    }
}
