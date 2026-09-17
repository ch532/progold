package com.example.consumer;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

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
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ProgoldEndToEndTest {

    @Test
    public void publishedSdkCompletesAgentActionTurn()
            throws Exception {

        final ServerSocket server =
                new ServerSocket(0);

        final int port =
                server.getLocalPort();

        final String[] receivedRequests =
                new String[2];

        Thread serverThread =
                new Thread(() -> {

                    try {

                        for (int i = 0; i < 2; i++) {

                            Socket socket =
                                    server.accept();

                            BufferedReader reader =
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    socket.getInputStream(),
                                                    StandardCharsets.UTF_8
                                            )
                                    );

                            String line;
                            int contentLength = 0;

                            while ((line = reader.readLine()) != null) {

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

                            receivedRequests[i] =
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
                                                                        "Consumer Test Device"
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
                        }

                    } catch (Exception exception) {

                        exception.printStackTrace();

                    } finally {

                        try {
                            server.close();
                        } catch (Exception ignored) {
                        }
                    }
                });

        serverThread.start();

        Context context =
                InstrumentationRegistry
                        .getInstrumentation()
                        .getTargetContext()
                        .getApplicationContext();

        assertNotNull(context);

        Progold.initialize(context);

        AgentConfig config =
                new AgentConfig.Builder(
                        "http://127.0.0.1:" + port
                ).build();

        Agent agent =
                Progold.connect(config);

        assertNotNull(agent);

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

                    assertEquals(
                            "get_device_info",
                            action.getName()
                    );

                    try {

                        return AgentActionResult.success(
                                action.getId(),
                                new JSONObject()
                                        .put(
                                                "model",
                                                "Consumer Test Device"
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
                        agent.createSession().getSessionId(),
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

            throw new AssertionError(
                    "Progold execution failed: "
                            + "successful="
                            + result.isSuccessful()
                            + ", turns="
                            + result.getTurns()
                            + ", error="
                            + result.getError()
                            + ", response="
                            + result.getResponse()
            );
        }

        assertEquals(
                2,
                result.getTurns()
        );

        assertNotNull(
                receivedRequests[0]
        );

        assertNotNull(
                receivedRequests[1]
        );

        assertTrue(
                receivedRequests[0].contains(
                        "\"task\":\"Get device information\""
                )
        );

        assertTrue(
                receivedRequests[1].contains(
                        "Consumer Test Device"
                )
        );

        assertTrue(
                receivedRequests[1].contains(
                        "completed"
                )
        );
    }
}
