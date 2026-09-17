package com.pro.gold;

import org.json.JSONObject;

public final class AgentCapability {

    private final String name;
    private final String description;
    private final String version;
    private final boolean enabled;
    private final JSONObject metadata;

    public AgentCapability(
            String name,
            String description
    ) {
        this(
                name,
                description,
                "1.0",
                true,
                new JSONObject()
        );
    }

    public AgentCapability(
            String name,
            String description,
            String version,
            boolean enabled,
            JSONObject metadata
    ) {

        if (name == null ||
                name.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Capability name cannot be empty"
            );
        }

        this.name = name;

        this.description =
                description == null
                        ? ""
                        : description;

        this.version =
                version == null ||
                        version.trim().isEmpty()
                        ? "1.0"
                        : version;

        this.enabled = enabled;

        this.metadata =
                metadata == null
                        ? new JSONObject()
                        : metadata;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public JSONObject getMetadata() {
        return metadata;
    }

    public JSONObject toJson() {

        JSONObject json =
                new JSONObject();

        try {

            json.put(
                    "name",
                    name
            );

            json.put(
                    "description",
                    description
            );

            json.put(
                    "version",
                    version
            );

            json.put(
                    "enabled",
                    enabled
            );

            json.put(
                    "metadata",
                    metadata
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to serialize agent capability",
                    exception
            );
        }

        return json;
    }
}
