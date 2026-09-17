package com.pro.gold;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AgentTransport {

    private final AgentHttpClient client;
    private final ExecutorService executor;

    public AgentTransport() {
        this(
                new AgentHttpClient()
        );
    }

    public AgentTransport(
            AgentHttpClient client
    ) {

        if (client == null) {
            throw new IllegalArgumentException(
                    "AgentHttpClient cannot be null"
            );
        }

        this.client = client;

        this.executor =
                Executors.newCachedThreadPool();
    }

    public AgentResponse send(
            AgentConfig config,
            AgentRequest request
    ) {

        return client.send(
                config,
                request
        );
    }

    public void sendAsync(
            AgentConfig config,
            AgentRequest request,
            AgentCallback callback
    ) {

        if (callback == null) {
            throw new IllegalArgumentException(
                    "AgentCallback cannot be null"
            );
        }

        executor.execute(() -> {

            AgentResponse response =
                    client.send(
                            config,
                            request
                    );

            if (response.isSuccessful()) {

                callback.onSuccess(
                        response
                );

            } else {

                callback.onFailure(
                        response
                );
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}
