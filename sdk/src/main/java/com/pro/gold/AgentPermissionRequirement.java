package com.pro.gold;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AgentPermissionRequirement {

    private final String actionName;
    private final List<AgentPermission>
            permissions;

    public AgentPermissionRequirement(
            String actionName
    ) {

        this(
                actionName,
                Collections.emptyList()
        );
    }

    public AgentPermissionRequirement(
            String actionName,
            List<AgentPermission> permissions
    ) {

        if (actionName == null ||
                actionName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Action name cannot be empty"
            );
        }

        this.actionName = actionName;

        if (permissions == null) {

            this.permissions =
                    Collections.emptyList();

        } else {

            this.permissions =
                    Collections.unmodifiableList(
                            new ArrayList<>(
                                    permissions
                            )
                    );
        }
    }

    public String getActionName() {
        return actionName;
    }

    public List<AgentPermission>
    getPermissions() {

        return permissions;
    }

    public boolean requiresPermissions() {
        return !permissions.isEmpty();
    }

    public JSONArray toJson() {

        JSONArray array =
                new JSONArray();

        for (AgentPermission permission :
                permissions) {

            if (permission == null) {
                continue;
            }

            array.put(
                    permission.getPermission()
            );
        }

        return array;
    }
}
