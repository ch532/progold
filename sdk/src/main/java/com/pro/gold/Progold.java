package com.pro.gold;

import android.content.Context;

public final class Progold {

    private static volatile Context applicationContext;

    private Progold() {
    }

    public static void initialize(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }

        applicationContext = context.getApplicationContext();
    }

    public static String getVersion() {
        return "1.0.0";
    }

    public static Agent connect(AgentConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config must not be null");
        }

        Context context = applicationContext;

        if (context == null) {
            return new Agent(config);
        }

        return new Agent(config, context);
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }
}
