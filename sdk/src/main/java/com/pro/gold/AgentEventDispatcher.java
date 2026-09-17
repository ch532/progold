package com.pro.gold;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AgentEventDispatcher {

    private final Set<AgentEventListener> listeners =
            new CopyOnWriteArraySet<>();

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public void addListener(
            AgentEventListener listener
    ) {

        if (listener == null) {
            throw new IllegalArgumentException(
                    "Event listener cannot be null"
            );
        }

        listeners.add(listener);
    }

    public void removeListener(
            AgentEventListener listener
    ) {

        if (listener == null) {
            return;
        }

        listeners.remove(listener);
    }

    public int listenerCount() {
        return listeners.size();
    }

    public void dispatch(
            AgentEvent event
    ) {

        if (event == null ||
                listeners.isEmpty()) {

            return;
        }

        for (AgentEventListener listener :
                listeners) {

            executor.execute(() -> {

                try {

                    listener.onEvent(event);

                } catch (Exception ignored) {
                    // One listener must not affect
                    // other listeners.
                }
            });
        }
    }

    public void shutdown() {

        executor.shutdown();
        listeners.clear();
    }
}
