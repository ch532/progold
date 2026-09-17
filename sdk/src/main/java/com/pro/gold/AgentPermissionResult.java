package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AgentPermissionResult {

    private final boolean granted;
    private final List<String> missingPermissions;

    private AgentPermissionResult(
            boolean granted,
            List<String> missingPermissions
    ) {

        this.granted = granted;

        this.missingPermissions =
                Collections.unmodifiableList(
                        new ArrayList<>(
                                missingPermissions
                        )
                );
    }

    public static AgentPermissionResult granted() {

        return new AgentPermissionResult(
                true,
                Collections.emptyList()
        );
    }

    public static AgentPermissionResult denied(
            List<String> missingPermissions
    ) {

        if (missingPermissions == null) {

            missingPermissions =
                    Collections.emptyList();
        }

        return new AgentPermissionResult(
                false,
                missingPermissions
        );
    }

    public boolean isGranted() {
        return granted;
    }

    public List<String>
    getMissingPermissions() {

        return missingPermissions;
    }

    public JSONObject toJson() {

        JSONObject json =
                new JSONObject();

        try {

            json.put(
                    "granted",
                    granted
            );

            JSONArray missing =
                    new JSONArray();

            for (String permission :
                    missingPermissions) {

                missing.put(permission);
            }

            json.put(
                    "missingPermissions",
                    missing
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to serialize permission result",
                    exception
            );
        }

        return json;
    }
}
