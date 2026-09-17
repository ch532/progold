package com.example.consumer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.pro.gold.ActionSchema;
import com.pro.gold.Agent;
import com.pro.gold.AgentActionResult;
import com.pro.gold.AgentCapability;
import com.pro.gold.AgentConfig;
import com.pro.gold.AgentExecutionConfig;
import com.pro.gold.AgentExecutionResult;
import com.pro.gold.AgentExecutor;
import com.pro.gold.AgentTask;
import com.pro.gold.Progold;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textView =
                new TextView(this);

        textView.setTextSize(18);
        textView.setPadding(
                32,
                32,
                32,
                32
        );

        textView.setText(
                "PROGOLD SDK\n\n" +
                "Running end-to-end test..."
        );

        setContentView(textView);

        new Thread(
                this::runTest
        ).start();
    }

    private void runTest() {

        ServerSocket server = null;

        try {

            server =
                    new ServerSocket(0);

            final int port =
                    server.getLocalPort();

            ServerSocket finalServer =
                    server;

            Thread serverThread =
                    new Thread(() -> {

                        try {

                            for (int i = 0; i < 2; i++) {

                                Socket socket =
                                        finalServer.accept();

                                BufferedReader reader =
                                        new BufferedReader(
                                                new InputStreamReader(
                                                        socket.getInputStream(),
                                                        StandardCharsets.UTF_8
                                                )
                                        );

                                String line;
                                int contentLength = 0;

                                while ((line =
                                        reader.readLine()) != null) {

                                    if (line.isEmpty()) {
                                        break;
                                    }

                                    if (line.toLowerCase()
                                            .startsWith(
                                                    "content-length:"
                                            )) {

                                        contentLength =
                                                Integer.parseInt(
                                                        line.substring(
                                                                line.indexOf(':') + 1
                                                        ).trim()
                                                );
                                    }
                                }

                                char[] body =
                                        new char[contentLength];

                                int offset = 0;

                                while (offset < contentLength) {

                                    int count =
                                            reader.read(
                                                    body,
                                                    offset,
                                                    contentLength - offset
                                            );

                                    if (count == -1) {
                                        break;
                                    }

                                    offset += count;
                                }

                                String requestBody =
                                        new String(
                                                body,
                                                0,
                                                offset
                                        );

                                String response;

                                if (i == 0) {

                                    response =
                                            new JSONObject()
                                                    .put(
                                                            "protocol",
                                                            "progold"
                                                    )
                                                    .put(
                                                            "version",
                                                            "1.0"
                                                    )
                                                    .put(
                                                            "requestId",
                                                            "response-001"
                                                    )
                                                    .put(
                                                            "status",
                                                            "requires_action"
                                                    )
                                                    .put(
                                                            "actions",
                                                            new JSONArray()
                                                                    .put(
                                                                            new JSONObject()
                                                                                    .put(
                                                                                            "id",
                                                                                            "action-001"
                                                                                    )
                                                                                    .put(
                                                                                            "name",
                                                                                            "get_device_info"
                                                                                    )
                                                                                    .put(
                                                                                            "parameters",
                                                                                            new JSONObject()
                                                                                    )
                                                                    )
                                                    )
                                                    .toString();

                                } else {

                                    response =
                                            new JSONObject()
                                                    .put(
                                                            "protocol",
                                                            "progold"
                                                    )
                                                    .put(
                                                            "version",
                                                            "1.0"
                                                    )
                                                    .put(
                                                            "requestId",
                                                            "response-002"
                                                    )
                                                    .put(
                                                            "status",
                                                            "completed"
                                                    )
                                                    .put(
                                                            "message",
                                                            "Device information received"
                                                    )
                                                    .put(
                                                            "result",
                                                            new JSONObject()
                                                                    .put(
                                                                            "model",
                                                                            "Huawei P30 Consumer Test"
                                                                    )
                                                                    .put(
                                                                            "sdk",
                                                                            29
                                                                    )
                                                    )
                                                    .toString();
                                }

                                byte[] responseBytes =
                                        response.getBytes(
                                                StandardCharsets.UTF_8
                                        );

                                OutputStream output =
                                        socket.getOutputStream();

                                output.write(
                                        (
                                                "HTTP/1.1 200 OK\r\n" +
                                                "Content-Type: application/json\r\n" +
                                                "Content-Length: " +
                                                responseBytes.length +
                                                "\r\n" +
                                                "Connection: close\r\n" +
                                                "\r\n"
                                        ).getBytes(
                                                StandardCharsets.UTF_8
                                        )
                                );

                                output.write(
                                        responseBytes
                                );

                                output.flush();

                                socket.close();

                                System.out.println(
                                        "REQUEST " +
                                                i +
                                                ": " +
                                                requestBody
                                );
                            }

                        } catch (Exception exception) {

                            exception.printStackTrace();

                        } finally {

                            try {
                                finalServer.close();
                            } catch (Exception ignored) {
                            }
                        }

                    });

            serverThread.start();

            Progold.initialize(
                    getApplicationContext()
            );

            AgentConfig config =
                    new AgentConfig.Builder(
                            "http://127.0.0.1:" + port
                    ).build();

            Agent agent =
                    Progold.connect(config);

            if (agent == null) {
                throw new IllegalStateException(
                        "Progold.connect() returned null"
                );
            }

            agent.registerCapability(
                    new AgentCapability(
                            "device",
                            "Device information"
                    ),
                    new ActionSchema(
                            "get_device_info",
                            "Get device information"
                    ),
                    action -> {

                        try {

                            return AgentActionResult.success(
                                    action.getId(),
                                    new JSONObject()
                                            .put(
                                                    "model",
                                                    "Huawei P30 Consumer Test"
                                            )
                                            .put(
                                                    "sdk",
                                                    29
                                            )
                            );

                        } catch (Exception exception) {

                            return AgentActionResult.failure(
                                    action.getId(),
                                    exception.getMessage()
                            );
                        }
                    }
            );

            agent.authorizeAction(
                    "get_device_info"
            );

            AgentTask task =
                    agent.createTask(
                            agent.createSession()
                                    .getSessionId(),
                            "Get device information"
                    );

            AgentExecutor executor =
                    new AgentExecutor(
                            agent,
                            new AgentExecutionConfig(5)
                    );

            AgentExecutionResult result =
                    executor.execute(task);

            executor.shutdown();
            agent.shutdown();

            serverThread.join(5000);

            if (!result.isSuccessful()) {

                throw new IllegalStateException(
                        "Execution failed: " +
                                result.getError()
                );
            }

            if (result.getTurns() != 2) {

                throw new IllegalStateException(
                        "Expected 2 turns but got " +
                                result.getTurns()
                );
            }

            showResult(
                    "PROGOLD SDK TEST PASSED\n\n" +
                            "SDK version: " +
                            Progold.getVersion() +
                            "\n\n" +
                            "Agent created: YES" +
                            "\nCapability: device" +
                            "\nAction: get_device_info" +
                            "\nAuthorization: GRANTED" +
                            "\nTurns: " +
                            result.getTurns() +
                            "\n\n" +
                            "Published Maven AAR successfully " +
                            "executed on the Android device."
            );

        } catch (Exception exception) {

            if (server != null) {

                try {
                    server.close();
                } catch (Exception ignored) {
                }
            }

            showResult(
                    "PROGOLD SDK TEST FAILED\n\n" +
                            exception.getClass()
                                    .getSimpleName() +
                            "\n\n" +
                            exception.getMessage()
            );
        }
    }

    private void showResult(
            String message
    ) {

        runOnUiThread(() ->
                textView.setText(message)
        );
    }
}
