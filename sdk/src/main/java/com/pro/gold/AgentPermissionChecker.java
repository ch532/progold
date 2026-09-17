package com.pro.gold;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AgentPermissionChecker {

    private final Context context;

    public AgentPermissionChecker(
            Context context
    ) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        this.context =
                context.getApplicationContext();
    }

    public boolean isGranted(
            String permission
    ) {

        if (permission == null ||
                permission.trim().isEmpty()) {

            return false;
        }

        return context.checkSelfPermission(
                permission
        ) == PackageManager.PERMISSION_GRANTED;
    }

    public List<String> getMissingPermissions(
            List<AgentPermission> permissions
    ) {

        if (permissions == null ||
                permissions.isEmpty()) {

            return Collections.emptyList();
        }

        List<String> missing =
                new ArrayList<>();

        for (AgentPermission permission :
                permissions) {

            if (permission == null) {
                continue;
            }

            if (!permission.isRequired()) {
                continue;
            }

            if (!isGranted(
                    permission.getPermission()
            )) {

                missing.add(
                        permission.getPermission()
                );
            }
        }

        return Collections.unmodifiableList(
                missing
        );
    }

    public boolean areGranted(
            List<AgentPermission> permissions
    ) {

        return getMissingPermissions(
                permissions
        ).isEmpty();
    }
}
