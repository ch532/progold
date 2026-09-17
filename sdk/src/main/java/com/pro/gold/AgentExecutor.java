package com.pro.gold;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public final class AgentExecutor {

    private final Agent agent;
    private final AgentExecutionConfig config;
    private final AgentEventDispatcher eventDispatcher;

    public AgentExecutor(
            Agent agent
    ) {
        this(
                agent,
                new AgentExecutionConfig()
        );
    }

    public AgentExecutor(
            Agent agent,
            AgentExecutionConfig config
    ) {
        if (agent == null) {
            throw new IllegalArgumentException(
                    "Agent cannot be null"
            );
        }

        if (config == null) {
            throw new IllegalArgumentException(
                    "Execution config cannot be null"
            );
        }

        this.agent = agent;
        this.config = config;
        this.eventDispatcher =
                new AgentEventDispatcher();
    }

    public AgentEventDispatcher
            getEventDispatcher() {
        return eventDispatcher;
    }

    public void addEventListener(
            AgentEventListener listener
    ) {
        eventDispatcher.addListener(
                listener
        );
    }

    public void removeEventListener(
            AgentEventListener listener
    ) {
        eventDispatcher.removeListener(
                listener
        );
    }

    public AgentExecutionResult execute(
            AgentTask task
    ) {
        if (task == null) {
            throw new IllegalArgumentException(
                    "Agent task cannot be null"
            );
        }

        AgentConversation conversation =
                agent.getConversation(
                        task.getSessionId()
                );

        if (conversation == null) {
            throw new IllegalArgumentException(
                    "Session does not exist: "
                            + task.getSessionId()
            );
        }

        dispatch(
                AgentEvent.Type.TASK_STARTED,
                task
        );

        task.start();

        if (conversation.size() == 0) {
            conversation.addUserMessage(
                    task.getTask()
            );
        }

        AgentResponse response;

        try {
            AgentRequest request =
                    agent.createRequest(task);

            dispatch(
                    AgentEvent.Type.REQUEST_SENT,
                    task
            );

            response =
                    agent.getTransport().send(
                            agent.getConfig(),
                            request
                    );

            dispatchResponse(
                    task,
                    response
            );

        } catch (Exception exception) {

            task.fail();

            dispatchFailure(
                    task,
                    exception.getMessage()
            );

            return AgentExecutionResult.failure(
                    1,
                    null,
                    exception.getMessage()
            );
        }

        int turns = 1;

        while (response != null &&
                response.isSuccessful() &&
                turns < config.getMaxTurns()) {

            AgentResponseParser.ParsedResponse parsed;

            try {
                parsed =
                        AgentResponseParser.parse(
                                response.getBody()
                        );
            } catch (Exception exception) {

                task.complete();

                dispatch(
                        AgentEvent.Type.TASK_COMPLETED,
                        task
                );

                return AgentExecutionResult.success(
                        turns,
                        response
                );
            }

            recordParsedMessage(
                    conversation,
                    parsed
            );

            /*
             * Capability negotiation is handled here
             * before checking for actions.
             */
            if (parsed.requiresNegotiation()) {

                try {
                    JSONObject negotiationResult =
                            agent.negotiateRemoteCapabilities(
                                    parsed.getNegotiation()
                            );

                    conversation.addMessage(
                            new AgentMessage(
                                    AgentMessage.Role.SYSTEM,
                                    negotiationResult.toString()
                            )
                    );

                    agent.sendNegotiationResponse(
                            task,
                            parsed.getNegotiation(),
                            negotiationResult
                    );

                } catch (Exception exception) {

                    task.fail();

                    dispatchFailure(
                            task,
                            exception.getMessage()
                    );

                    return AgentExecutionResult.failure(
                            turns,
                            response,
                            exception.getMessage()
                    );
                }
            }

            if (!parsed.requiresAction()) {

                task.complete();

                dispatch(
                        AgentEvent.Type.TASK_COMPLETED,
                        task
                );

                return AgentExecutionResult.success(
                        turns,
                        response
                );
            }

            List<AgentAction> actions =
                    parsed.getActions();

            dispatchActionsRequested(
                    task,
                    actions
            );

            /*
             * This is now the single action execution
             * authority for AgentExecutor.
             *
             * Agent.executeActions()
             * -> AgentActionExecutor
             * -> authorization
             * -> permission checking
             * -> capability handler
             */
            List<AgentActionResult> results =
                    agent.executeActions(
                            task,
                            actions
                    );

            dispatchActionsCompleted(
                    task,
                    results
            );

            AgentRequest followUp =
                    createFollowUpRequest(
                            task,
                            response,
                            results
                    );

            turns++;

            dispatch(
                    AgentEvent.Type.FOLLOW_UP_SENT,
                    task
            );

            try {

                response =
                        agent.getTransport().send(
                                agent.getConfig(),
                                followUp
                        );

                dispatchResponse(
                        task,
                        response
                );

            } catch (Exception exception) {

                task.fail();

                dispatchFailure(
                        task,
                        exception.getMessage()
                );

                return AgentExecutionResult.failure(
                        turns,
                        response,
                        exception.getMessage()
                );
            }
        }

        /*
         * We reached the configured maximum number
         * of turns while the agent still requested
         * another action.
         */
        if (response != null &&
                response.isSuccessful()) {

            try {

                AgentResponseParser.ParsedResponse parsed =
                        AgentResponseParser.parse(
                                response.getBody()
                        );

                if (parsed.requiresAction()) {

                    task.fail();

                    dispatchFailure(
                            task,
                            "Maximum agent turns reached"
                    );

                    return AgentExecutionResult.failure(
                            turns,
                            response,
                            "Maximum agent turns reached"
                    );
                }

            } catch (Exception ignored) {
                /*
                 * Treat a non-Progold response
                 * as a final response.
                 */
            }

            task.complete();

            dispatch(
                    AgentEvent.Type.TASK_COMPLETED,
                    task
            );

            return AgentExecutionResult.success(
                    turns,
                    response
            );
        }

        String error =
                response == null
                        ? "Agent response is null"
                        : response.getError();

        if (error == null ||
                error.trim().isEmpty()) {

            error = "Agent request failed";
        }

        task.fail();

        dispatchFailure(
                task,
                error
        );

        return AgentExecutionResult.failure(
                turns,
                response,
                error
        );
    }

    private AgentRequest createFollowUpRequest(
            AgentTask task,
            AgentResponse previousResponse,
            List<AgentActionResult> results
    ) {
        JSONObject context =
                new JSONObject();

        try {

            context.put(
                    "taskId",
                    task.getTaskId()
            );

            context.put(
                    "previousResponse",
                    previousResponse.getBody()
            );

            JSONArray actionResults =
                    new JSONArray();

            for (AgentActionResult result :
                    results) {

                actionResults.put(
                        result.toJson()
                );
            }

            context.put(
                    "actionResults",
                    actionResults
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to create follow-up request",
                    exception
            );
        }

        return new AgentRequest(
                task.getSessionId(),
                "Continue the current task using the action results.",
                context,
                new JSONArray()
        );
    }

    private void recordParsedMessage(
            AgentConversation conversation,
            AgentResponseParser.ParsedResponse parsed
    ) {
        if (conversation == null ||
                parsed == null) {
            return;
        }

        String message =
                parsed.getMessage();

        if (message != null &&
                !message.trim().isEmpty()) {

            conversation.addAgentMessage(
                    message
            );
        }
    }

    private void dispatchResponse(
            AgentTask task,
            AgentResponse response
    ) {
        JSONObject data =
                new JSONObject();

        try {

            data.put(
                    "successful",
                    response != null &&
                            response.isSuccessful()
            );

            if (response != null) {

                data.put(
                        "statusCode",
                        response.getStatusCode()
                );

                data.put(
                        "body",
                        response.getBody()
                );
            }

        } catch (Exception ignored) {
        }

        dispatch(
                AgentEvent.Type.RESPONSE_RECEIVED,
                task,
                data
        );
    }

    private void dispatchActionsRequested(
            AgentTask task,
            List<AgentAction> actions
    ) {
        JSONObject data =
                new JSONObject();

        try {

            JSONArray array =
                    new JSONArray();

            for (AgentAction action :
                    actions) {

                array.put(
                        action.toJson()
                );
            }

            data.put(
                    "actions",
                    array
            );

        } catch (Exception ignored) {
        }

        dispatch(
                AgentEvent.Type.ACTION_REQUESTED,
                task,
                data
        );
    }

    private void dispatchActionsCompleted(
            AgentTask task,
            List<AgentActionResult> results
    ) {
        JSONObject data =
                new JSONObject();

        try {

            JSONArray array =
                    new JSONArray();

            for (AgentActionResult result :
                    results) {

                array.put(
                        result.toJson()
                );
            }

            data.put(
                    "results",
                    array
            );

        } catch (Exception ignored) {
        }

        dispatch(
                AgentEvent.Type.ACTION_COMPLETED,
                task,
                data
        );
    }

    private void dispatchFailure(
            AgentTask task,
            String error
    ) {
        JSONObject data =
                new JSONObject();

        try {

            data.put(
                    "error",
                    error == null
                            ? "Unknown error"
                            : error
            );

        } catch (Exception ignored) {
        }

        dispatch(
                AgentEvent.Type.TASK_FAILED,
                task,
                data
        );
    }

    private void dispatch(
            AgentEvent.Type type,
            AgentTask task
    ) {
        dispatch(
                type,
                task,
                new JSONObject()
        );
    }

    private void dispatch(
            AgentEvent.Type type,
            AgentTask task,
            JSONObject data
    ) {
        eventDispatcher.dispatch(
                new AgentEvent(
                        type,
                        task.getTaskId(),
                        data
                )
        );
    }

    public void shutdown() {
        eventDispatcher.shutdown();
    }
}
