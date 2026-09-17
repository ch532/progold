package com.pro.gold;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class AgentHttpClient {

    private final int connectTimeoutMs;
    private final int readTimeoutMs;

    public AgentHttpClient() {
        this(
                15000,
                30000
        );
    }

    public AgentHttpClient(
            int connectTimeoutMs,
            int readTimeoutMs
    ) {

        if (connectTimeoutMs <= 0) {
            throw new IllegalArgumentException(
                    "Connect timeout must be greater than zero"
            );
        }

        if (readTimeoutMs <= 0) {
            throw new IllegalArgumentException(
                    "Read timeout must be greater than zero"
            );
        }

        this.connectTimeoutMs =
                connectTimeoutMs;

        this.readTimeoutMs =
                readTimeoutMs;
    }

    public AgentResponse send(
            AgentConfig config,
            AgentRequest request
    ) {

        if (config == null) {
            throw new IllegalArgumentException(
                    "AgentConfig cannot be null"
            );
        }

        if (request == null) {
            throw new IllegalArgumentException(
                    "AgentRequest cannot be null"
            );
        }

        HttpURLConnection connection = null;

        try {

            URL url =
                    new URL(config.getEndpoint());

            connection =
                    (HttpURLConnection)
                            url.openConnection();

            connection.setRequestMethod("POST");
            connection.setConnectTimeout(
                    connectTimeoutMs
            );
            connection.setReadTimeout(
                    readTimeoutMs
            );

            connection.setDoOutput(true);

            connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );

            connection.setRequestProperty(
                    "Accept",
                    "application/json"
            );

            String apiKey =
                    config.getApiKey();

            if (apiKey != null &&
                    !apiKey.trim().isEmpty()) {

                connection.setRequestProperty(
                        "Authorization",
                        "Bearer " + apiKey
                );
            }

            byte[] payload =
                    request.toJson()
                            .toString()
                            .getBytes(
                                    StandardCharsets.UTF_8
                            );

            connection.setFixedLengthStreamingMode(
                    payload.length
            );

            try (OutputStream output =
                         connection.getOutputStream()) {

                output.write(payload);
                output.flush();
            }

            int statusCode =
                    connection.getResponseCode();

            InputStream stream;

            if (statusCode >= 200 &&
                    statusCode < 400) {

                stream =
                        connection.getInputStream();

            } else {

                stream =
                        connection.getErrorStream();
            }

            String body =
                    readBody(stream);

            if (statusCode >= 200 &&
                    statusCode < 300) {

                return AgentResponse.success(
                        statusCode,
                        body
                );
            }

            return AgentResponse.failure(
                    statusCode,
                    body == null ||
                            body.trim().isEmpty()
                            ? "Agent request failed"
                            : body
            );

        } catch (Exception exception) {

            return AgentResponse.failure(
                    -1,
                    exception.getMessage()
                            == null
                            ? exception.getClass()
                                    .getSimpleName()
                            : exception.getMessage()
            );

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readBody(
            InputStream stream
    ) throws Exception {

        if (stream == null) {
            return "";
        }

        StringBuilder result =
                new StringBuilder();

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     stream,
                                     StandardCharsets.UTF_8
                             )
                     )) {

            String line;

            while ((line = reader.readLine())
                    != null) {

                result.append(line);
            }
        }

        return result.toString();
    }
}
