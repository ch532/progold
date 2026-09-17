package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CapabilityNegotiationResult {

    private final String capabilityName;
    private final CapabilityState state;
    private final List<String> missingPermissions;
    private final String reason;

    public CapabilityNegotiationResult(
            String capabilityName,
            CapabilityState state,
            List<String> missingPermissions,
            String reason) {

        if (capabilityName == null || capabilityName.trim().isEmpty()) {
            throw new IllegalArgumentException("capabilityName must not be empty");
        }

        if (state == null) {
            throw new IllegalArgumentException("state must not be null");
        }

        this.capabilityName = capabilityName;
        this.state = state;

        this.missingPermissions =
                missingPermissions == null
                        ? Collections.emptyList()
                        : Collections.unmodifiableList(
                                new ArrayList<>(missingPermissions)
                        );

        this.reason = reason;
    }

    public String getCapabilityName() {
        return capabilityName;
    }

    public CapabilityState getState() {
        return state;
    }

    public List<String> getMissingPermissions() {
        return missingPermissions;
    }

    public String getReason() {
        return reason;
    }

    public boolean isReady() {
        return state == CapabilityState.READY;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("capabilityName", capabilityName);
            json.put("state", state.name());

            JSONArray permissions = new JSONArray();

            for (String permission : missingPermissions) {
                permissions.put(permission);
            }

            json.put("missingPermissions", permissions);

            if (reason != null && !reason.isEmpty()) {
                json.put("reason", reason);
            }

        } catch (JSONException e) {
            throw new IllegalStateException(
                    "Failed to serialize capability negotiation result",
                    e
            );
        }

        return json;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
