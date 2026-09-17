package com.pro.gold;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class CapabilityRegistry {

    private final Map<String, AgentCapability> capabilities =
            new ConcurrentHashMap<>();

    public void register(
            AgentCapability capability
    ) {

        if (capability == null) {
            throw new IllegalArgumentException(
                    "Capability cannot be null"
            );
        }

        capabilities.put(
                capability.getName(),
                capability
        );
    }

    public void unregister(
            String capabilityName
    ) {

        if (capabilityName == null) {
            return;
        }

        capabilities.remove(
                capabilityName
        );
    }

    public boolean contains(
            String capabilityName
    ) {

        if (capabilityName == null) {
            return false;
        }

        return capabilities.containsKey(
                capabilityName
        );
    }

    public AgentCapability get(
            String capabilityName
    ) {

        if (capabilityName == null) {
            return null;
        }

        return capabilities.get(
                capabilityName
        );
    }

    public int size() {
        return capabilities.size();
    }

    public Set<String> getNames() {

        return Collections.unmodifiableSet(
                new HashSet<>(
                        capabilities.keySet()
                )
        );
    }

    public List<AgentCapability>
    getCapabilities() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        capabilities.values()
                )
        );
    }

    public JSONArray toJson() {

        JSONArray array =
                new JSONArray();

        for (AgentCapability capability :
                capabilities.values()) {

            array.put(
                    capability.toJson()
            );
        }

        return array;
    }

    public void clear() {
        capabilities.clear();
    }
}
