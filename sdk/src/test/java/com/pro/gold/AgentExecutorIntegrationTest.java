package com.pro.gold;

import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AgentExecutorIntegrationTest {

    @Test
    public void completeAgenticLoopExecutesActionAndSendsResult()
            throws Exception {

        try (ServerSocket server =
                     new ServerSocket(0)) {

            AtomicReference<String>
                    firstRequest =
                    new AtomicReference<>();

            AtomicReference<String>
                    secondRequest =
                    new AtomicReference<>();

            Thread serverThread =
                    new Thread(() -> {

                        try {

                            handleRequest(
                                    server,
                                    firstRequest,
                                    actionResponse()
                            );

                            handleRequest(
                                    server,
                                    secondRequest,
                                    finalResponse()
                            );

                        } catch (Exception exception) {

                            throw new RuntimeException(
                                    exception
                            );
                        }
                    });

            serverThread.start();

            AgentConfig config =
                    new AgentConfig.Builder(
                            "http://127.0.0.1:"
                                    + server.getLocalPort()
                                    + "/agent"
                    ).build();

            Agent agent =
                    new Agent(config);

            agent.registerCapability(
                    new AgentCapability(
                            "device",
                            "Device information"
                    ),
                    new ActionSchema(
                            "get_device_info",
                            "Get device information",
                            new JSONObject()
                    ),
                    action -> {

                        try {

                            JSONObject result =
                                    new JSONObject();

                            result.put(
                                    "model",
                                    "Test Device"
                            );

                            result.put(
                                    "sdk",
                                    36
                            );

                            return AgentActionResult.success(
                                    action.getId(),
                                    result
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

            AgentSession session =
                    agent.createSession();

            AgentTask task =
                    agent.createTask(
                            session.getSessionId(),
                            "Get the device information"
                    );

            AgentExecutor executor =
                    new AgentExecutor(agent);

            AgentExecutionResult result =
                    executor.execute(task);

            serverThread.join(5000);

            assertNotNull(
                    firstRequest.get()
            );

            assertNotNull(
                    secondRequest.get()
            );

            assertTrue(
                    result.isSuccessful()
            );

            assertEquals(
                    2,
                    result.getTurns()
            );

            assertTrue(
                    secondRequest.get()
                            .contains(
                                    "\"actionResults\""
                            )
            );

            assertTrue(
                    secondRequest.get()
                            .contains(
                                    "Test Device"
                            )
            );

            assertTrue(
                    secondRequest.get()
                            .contains(
                                    "\"status\":\"completed\""
                            )
            );

            agent.shutdown();
            executor.shutdown();
        }
    }

    private static String actionResponse() {

        return "{"
                + "\"protocol\":\"progold\","
                + "\"version\":\"1.0\","
                + "\"status\":\"success\","
                + "\"message\":\"I need device information.\","
                + "\"actions\":[{"
                + "\"id\":\"action-1\","
                + "\"name\":\"get_device_info\","
                + "\"parameters\":{}"
                + "}]"
                + "}";
    }

    private static String finalResponse() {

        return "{"
                + "\"protocol\":\"progold\","
                + "\"version\":\"1.0\","
                + "\"status\":\"success\","
                + "\"message\":\"Device information received.\""
                + "}";
    }

    private static void handleRequest(
            ServerSocket server,
            AtomicReference<String> requestReference,
            String response
    ) throws Exception {

        try (Socket socket =
                     server.accept()) {

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream(),
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder request =
                    new StringBuilder();

            String line;
            int contentLength = 0;

            while ((line = reader.readLine()) != null) {

                if (line.isEmpty()) {
                    break;
                }

                request.append(line)
                        .append('\n');

                String lower =
                        line.toLowerCase();

                if (lower.startsWith(
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

                int read =
                        reader.read(
                                body,
                                offset,
                                contentLength - offset
                        );

                if (read == -1) {
                    break;
                }

                offset += read;
            }

            request.append(
                    new String(body)
            );

            requestReference.set(
                    request.toString()
            );

            byte[] responseBytes =
                    response.getBytes(
                            StandardCharsets.UTF_8
                    );

            OutputStream output =
                    socket.getOutputStream();

            String headers =
                    "HTTP/1.1 200 OK\r\n"
                            + "Content-Type: application/json\r\n"
                            + "Content-Length: "
                            + responseBytes.length
                            + "\r\n"
                            + "Connection: close\r\n"
                            + "\r\n";

            output.write(
                    headers.getBytes(
                            StandardCharsets.UTF_8
                    )
            );

            output.write(
                    responseBytes
            );

            output.flush();
        }
    }
}
