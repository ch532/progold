package com.pro.gold;

import org.json.JSONObject;

import java.util.UUID;

/**
 * Represents an action requested by an agent.
 *
 * Progold transports the action request.
 * The host application decides whether to execute it.
 */
public final class AgentAction {

    private final String id;
    private final String name;
    private final JSONObject parameters;

    public AgentAction(String name) {
        this(
                UUID.randomUUID().toString(),
                name,
                new JSONObject()
        );
    }

    public AgentAction(
            String id,
            String name,
            JSONObject parameters
    ) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Action ID cannot be empty"
            );
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Action name cannot be empty"
            );
        }

        this.id = id;
        this.name = name;
        this.parameters = parameters == null
                ? new JSONObject()
                : parameters;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public JSONObject getParameters() {
        return parameters;
    }

    public JSONObject toJson() {

        JSONObject json = new JSONObject();

        try {
            json.put("id", id);
            json.put("name", name);
            json.put("parameters", parameters);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to create Progold action",
                    exception
            );
        }

        return json;
    }
}
