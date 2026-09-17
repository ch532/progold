package com.pro.gold;

import org.json.JSONObject;

/**
 * Describes an action exposed by the host application.
 */
public final class ActionSchema {

    private final String name;
    private final String description;
    private final JSONObject parameters;

    public ActionSchema(
            String name,
            String description
    ) {
        this(
                name,
                description,
                new JSONObject()
        );
    }

    public ActionSchema(
            String name,
            String description,
            JSONObject parameters
    ) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Action name cannot be empty"
            );
        }

        this.name = name;
        this.description =
                description == null ? "" : description;

        this.parameters =
                parameters == null
                        ? new JSONObject()
                        : parameters;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public JSONObject getParameters() {
        return parameters;
    }

    public JSONObject toJson() {

        JSONObject json = new JSONObject();

        try {
            json.put("name", name);
            json.put("description", description);
            json.put("parameters", parameters);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to create action schema",
                    exception
            );
        }

        return json;
    }
}
