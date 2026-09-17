package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AuthorizationRegistry {

    private final Map<String, AgentAuthorization>
            authorizations =
            new ConcurrentHashMap<>();

    public void register(
            String actionName
    ) {

        if (actionName == null ||
                actionName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Action name cannot be empty"
            );
        }

        authorizations.putIfAbsent(
                actionName,
                new AgentAuthorization(
                        actionName
                )
        );
    }

    public void authorize(
            String actionName
    ) {

        AgentAuthorization authorization =
                getOrCreate(actionName);

        authorization.authorize();
    }

    public void deny(
            String actionName
    ) {

        AgentAuthorization authorization =
                getOrCreate(actionName);

        authorization.deny();
    }

    public void reset(
            String actionName
    ) {

        AgentAuthorization authorization =
                authorizations.get(
                        actionName
                );

        if (authorization != null) {
            authorization.reset();
        }
    }

    public AgentAuthorization get(
            String actionName
    ) {

        if (actionName == null) {
            return null;
        }

        return authorizations.get(
                actionName
        );
    }

    public boolean isAuthorized(
            String actionName
    ) {

        AgentAuthorization authorization =
                get(actionName);

        return authorization != null &&
                authorization.isAuthorized();
    }

    public AgentAuthorization.Status
    getStatus(
            String actionName
    ) {

        AgentAuthorization authorization =
                get(actionName);

        if (authorization == null) {
            return AgentAuthorization.Status
                    .NOT_CONFIGURED;
        }

        return authorization.getStatus();
    }

    public void unregister(
            String actionName
    ) {

        if (actionName == null) {
            return;
        }

        authorizations.remove(
                actionName
        );
    }

    public int size() {
        return authorizations.size();
    }

    public List<String> getActionNames() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        authorizations.keySet()
                )
        );
    }

    public JSONArray toJson() {

        JSONArray array =
                new JSONArray();

        for (AgentAuthorization authorization :
                authorizations.values()) {

            JSONObject json =
                    new JSONObject();

            try {

                json.put(
                        "action",
                        authorization.getActionName()
                );

                json.put(
                        "status",
                        authorization.getStatus()
                                .name()
                                .toLowerCase()
                );

                json.put(
                        "authorized",
                        authorization.isAuthorized()
                );

            } catch (Exception exception) {

                throw new IllegalStateException(
                        "Unable to serialize authorization",
                        exception
                );
            }

            array.put(json);
        }

        return array;
    }

    public void clear() {
        authorizations.clear();
    }

    private AgentAuthorization getOrCreate(
            String actionName
    ) {

        if (actionName == null ||
                actionName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Action name cannot be empty"
            );
        }

        AgentAuthorization authorization =
                authorizations.get(actionName);

        if (authorization != null) {
            return authorization;
        }

        AgentAuthorization created =
                new AgentAuthorization(
                        actionName
                );

        AgentAuthorization existing =
                authorizations.putIfAbsent(
                        actionName,
                        created
                );

        return existing == null
                ? created
                : existing;
    }
}
