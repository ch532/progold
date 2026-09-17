package com.pro.gold;

import org.json.JSONObject;

public final class AgentTurn {

    public enum Type {
        REQUEST,
        RESPONSE,
        ACTION,
        ACTION_RESULT
    }

    private final String id;
    private final Type type;
    private final long timestamp;
    private final JSONObject data;

    public AgentTurn(
            Type type,
            JSONObject data
    ) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "Turn type cannot be null"
            );
        }

        this.id =
                java.util.UUID.randomUUID()
                        .toString();

        this.type = type;
        this.timestamp =
                System.currentTimeMillis();

        this.data =
                data == null
                        ? new JSONObject()
                        : data;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public JSONObject getData() {
        return data;
    }

    public JSONObject toJson() {

        JSONObject json =
                new JSONObject();

        try {

            json.put(
                    "id",
                    id
            );

            json.put(
                    "type",
                    type.name().toLowerCase()
            );

            json.put(
                    "timestamp",
                    timestamp
            );

            json.put(
                    "data",
                    data
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to serialize agent turn",
                    exception
            );
        }

        return json;
    }
}
