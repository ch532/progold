package com.pro.gold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CapabilityActionRegistry {

    private final Map<String, RegisteredCapability>
            capabilities =
            new ConcurrentHashMap<>();

    public void register(
            AgentCapability capability,
            ActionSchema actionSchema,
            AgentCapabilityHandler handler
    ) {

        RegisteredCapability registered =
                new RegisteredCapability(
                        capability,
                        actionSchema,
                        handler
                );

        capabilities.put(
                capability.getName(),
                registered
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

    public RegisteredCapability get(
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

    public List<RegisteredCapability>
    getCapabilities() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        capabilities.values()
                )
        );
    }

    public AgentActionResult execute(
            AgentAction action
    ) {

        if (action == null) {

            throw new IllegalArgumentException(
                    "Agent action cannot be null"
            );
        }

        RegisteredCapability registered =
                findByActionName(
                        action.getName()
                );

        if (registered == null) {

            return AgentActionResult.failure(
                    action.getId(),
                    "No capability is registered for action: "
                            + action.getName()
            );
        }

        try {

            AgentActionResult result =
                    registered.getHandler()
                            .handle(action);

            if (result == null) {

                return AgentActionResult.failure(
                        action.getId(),
                        "Capability handler returned null"
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

    private RegisteredCapability
    findByActionName(
            String actionName
    ) {

        for (RegisteredCapability registered :
                capabilities.values()) {

            if (registered
                    .getActionSchema()
                    .getName()
                    .equals(actionName)) {

                return registered;
            }
        }

        return null;
    }
}
