package com.pro.gold;

/**
 * Configuration used to connect Progold to an agentic AI endpoint.
 */
public final class AgentConfig {

    private final String endpoint;
    private final String apiKey;

    private AgentConfig(Builder builder) {
        this.endpoint = builder.endpoint;
        this.apiKey = builder.apiKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public static final class Builder {

        private String endpoint;
        private String apiKey;

        public Builder(String endpoint) {
            this.endpoint = endpoint;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public AgentConfig build() {
            if (endpoint == null || endpoint.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Progold agent endpoint cannot be empty"
                );
            }

            return new AgentConfig(this);
        }
    }
}
