package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry of actions explicitly made available
 * by the host application.
 */
public final class ActionRegistry {

    private final Map<String, RegisteredAction> actions =
            new ConcurrentHashMap<>();

    /**
     * Registers an action.
     */
    public void register(
            ActionSchema schema,
            AgentActionHandler handler
    ) {

        if (schema == null) {
            throw new IllegalArgumentException(
                    "Action schema cannot be null"
            );
        }

        if (handler == null) {
            throw new IllegalArgumentException(
                    "Action handler cannot be null"
            );
        }

        actions.put(
                schema.getName(),
                new RegisteredAction(
                        schema,
                        handler
                )
        );
    }

    /**
     * Convenience registration method.
     */
    public void register(
            String actionName,
            AgentActionHandler handler
    ) {

        register(
                new ActionSchema(
                        actionName,
                        ""
                ),
                handler
        );
    }

    /**
     * Removes an action.
     */
    public void unregister(String actionName) {

        if (actionName == null) {
            return;
        }

        actions.remove(actionName);
    }

    /**
     * Returns whether an action exists.
     */
    public boolean contains(String actionName) {

        if (actionName == null) {
            return false;
        }

        return actions.containsKey(actionName);
    }

    /**
     * Returns the number of registered actions.
     */
    public int size() {
        return actions.size();
    }

    /**
     * Returns an immutable snapshot of action names.
     */
    public Set<String> getActionNames() {

        return Collections.unmodifiableSet(
                new HashSet<>(
                        actions.keySet()
                )
        );
    }

    /**
     * Returns an action schema.
     */
    public ActionSchema getSchema(
            String actionName
    ) {

        RegisteredAction registered =
                actions.get(actionName);

        if (registered == null) {
            return null;
        }

        return registered.schema;
    }

    /**
     * Builds a capability description for the agent.
     */
    public JSONArray toJson() {

        JSONArray result =
                new JSONArray();

        for (RegisteredAction registered :
                actions.values()) {

            result.put(
                    registered.schema.toJson()
            );
        }

        return result;
    }

    /**
     * Executes a registered action.
     */
    public AgentActionResult execute(
            AgentAction action
    ) {

        if (action == null) {
            throw new IllegalArgumentException(
                    "Agent action cannot be null"
            );
        }

        RegisteredAction registered =
                actions.get(action.getName());

        if (registered == null) {

            return AgentActionResult.failure(
                    action.getId(),
                    "Action is not registered: "
                            + action.getName()
            );
        }

        try {

            AgentActionResult result =
                    registered.handler.handle(action);

            if (result == null) {

                return AgentActionResult.failure(
                        action.getId(),
                        "Action handler returned null"
                );
            }

            return result;

        } catch (Exception exception) {

            return AgentActionResult.failure(
                    action.getId(),
                    exception.getMessage()
            );
        }
    }

    private static final class RegisteredAction {

        private final ActionSchema schema;
        private final AgentActionHandler handler;

        private RegisteredAction(
                ActionSchema schema,
                AgentActionHandler handler
        ) {
            this.schema = schema;
            this.handler = handler;
        }
    }
}
