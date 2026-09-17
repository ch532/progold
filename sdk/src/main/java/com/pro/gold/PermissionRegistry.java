package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PermissionRegistry {

    private final Map<String,
            AgentPermissionRequirement>
            requirements =
            new ConcurrentHashMap<>();

    public void register(
            AgentPermissionRequirement requirement
    ) {

        if (requirement == null) {
            throw new IllegalArgumentException(
                    "Permission requirement cannot be null"
            );
        }

        requirements.put(
                requirement.getActionName(),
                requirement
        );
    }

    public void register(
            String actionName,
            AgentPermission... permissions
    ) {

        List<AgentPermission> list =
                new ArrayList<>();

        if (permissions != null) {

            Collections.addAll(
                    list,
                    permissions
            );
        }

        register(
                new AgentPermissionRequirement(
                        actionName,
                        list
                )
        );
    }

    public AgentPermissionRequirement get(
            String actionName
    ) {

        if (actionName == null) {
            return null;
        }

        return requirements.get(
                actionName
        );
    }

    public boolean contains(
            String actionName
    ) {

        if (actionName == null) {
            return false;
        }

        return requirements.containsKey(
                actionName
        );
    }

    public void unregister(
            String actionName
    ) {

        if (actionName == null) {
            return;
        }

        requirements.remove(
                actionName
        );
    }

    public int size() {
        return requirements.size();
    }

    public List<String> getActionNames() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        requirements.keySet()
                )
        );
    }

    public JSONArray toJson() {

        JSONArray array =
                new JSONArray();

        for (AgentPermissionRequirement requirement :
                requirements.values()) {

            JSONObject json =
                    new JSONObject();

            try {

                json.put(
                        "action",
                        requirement.getActionName()
                );

                json.put(
                        "permissions",
                        requirement.toJson()
                );

            } catch (Exception exception) {

                throw new IllegalStateException(
                        "Unable to serialize permission requirement",
                        exception
                );
            }

            array.put(json);
        }

        return array;
    }

    public void clear() {
        requirements.clear();
    }
}
