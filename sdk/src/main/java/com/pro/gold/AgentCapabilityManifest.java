package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;

public final class AgentCapabilityManifest {

    private static final String PROTOCOL =
            "progold";

    private static final String VERSION =
            "1.0";

    private final CapabilityRegistry registry;

    public AgentCapabilityManifest(
            CapabilityRegistry registry
    ) {

        if (registry == null) {
            throw new IllegalArgumentException(
                    "Capability registry cannot be null"
            );
        }

        this.registry = registry;
    }

    public CapabilityRegistry
    getRegistry() {

        return registry;
    }

    public JSONObject toJson() {

        JSONObject json =
                new JSONObject();

        try {

            json.put(
                    "protocol",
                    PROTOCOL
            );

            json.put(
                    "version",
                    VERSION
            );

            json.put(
                    "capabilities",
                    registry.toJson()
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to create capability manifest",
                    exception
            );
        }

        return json;
    }

    public JSONArray toJsonArray() {
        return registry.toJson();
    }

    public boolean isEmpty() {
        return registry.size() == 0;
    }

    public int size() {
        return registry.size();
    }
}
