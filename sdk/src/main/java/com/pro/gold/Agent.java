package com.pro.gold;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Agent {

    private final AgentConfig config;
    private final AgentTransport transport;
    private final AgentSessionManager sessionManager;
    private final AgentTaskManager taskManager;

    private final ActionRegistry actionRegistry;
    private final CapabilityRegistry capabilityRegistry;
    private final CapabilityActionRegistry capabilityActionRegistry;
    private final AuthorizationRegistry authorizationRegistry;
    private final PermissionRegistry permissionRegistry;
    private final AgentPermissionChecker permissionChecker;

    private final CapabilityNegotiation capabilityNegotiation;
    private final AgentCapabilityManifest capabilityManifest;
    private final AgentActionExecutor actionExecutor;

    private final Map<String, AgentConversation>
            conversations =
            new ConcurrentHashMap<>();

    public Agent(
            AgentConfig config
    ) {
        this(config, null);
    }

    public Agent(
            AgentConfig config,
            Context context
    ) {
        if (config == null) {
            throw new IllegalArgumentException(
                    "AgentConfig cannot be null"
            );
        }

        this.config = config;
        this.transport = new AgentTransport();
        this.sessionManager =
                new AgentSessionManager();
        this.taskManager =
                new AgentTaskManager();

        this.actionRegistry =
                new ActionRegistry();

        this.capabilityRegistry =
                new CapabilityRegistry();

        this.capabilityActionRegistry =
                new CapabilityActionRegistry();

        this.authorizationRegistry =
                new AuthorizationRegistry();

        this.permissionRegistry =
                new PermissionRegistry();

        this.permissionChecker =
                context == null
                        ? null
                        : new AgentPermissionChecker(
                                context
                        );

        this.capabilityNegotiation =
                new CapabilityNegotiation(
                        capabilityRegistry,
                        capabilityActionRegistry,
                        authorizationRegistry,
                        permissionRegistry,
                        permissionChecker
                );

        this.capabilityManifest =
                new AgentCapabilityManifest(
                        capabilityRegistry
                );

        this.actionExecutor =
                new AgentActionExecutor(
                        capabilityActionRegistry,
                        authorizationRegistry,
                        permissionRegistry,
                        permissionChecker
                );
    }

    public AgentConfig getConfig() {
        return config;
    }

    public AgentTransport getTransport() {
        return transport;
    }

    public AgentSessionManager getSessionManager() {
        return sessionManager;
    }

    public AgentTaskManager getTaskManager() {
        return taskManager;
    }

    public ActionRegistry getActionRegistry() {
        return actionRegistry;
    }

    public CapabilityRegistry getCapabilityRegistry() {
        return capabilityRegistry;
    }

    public CapabilityActionRegistry
            getCapabilityActionRegistry() {
        return capabilityActionRegistry;
    }

    public AuthorizationRegistry
            getAuthorizationRegistry() {
        return authorizationRegistry;
    }

    public PermissionRegistry
            getPermissionRegistry() {
        return permissionRegistry;
    }

    public AgentPermissionChecker
            getPermissionChecker() {
        return permissionChecker;
    }

    public CapabilityNegotiation
            getCapabilityNegotiation() {
        return capabilityNegotiation;
    }

    public AgentCapabilityManifest
            getCapabilityManifest() {
        return capabilityManifest;
    }

    public AgentActionExecutor
            getActionExecutor() {
        return actionExecutor;
    }

    public AgentSession createSession() {
        AgentSession session =
                sessionManager.create();

        conversations.put(
                session.getSessionId(),
                new AgentConversation(
                        session.getSessionId()
                )
        );

        return session;
    }

    public AgentSession getSession(
            String sessionId
    ) {
        return sessionManager.get(
                sessionId
        );
    }

    public AgentConversation getConversation(
            String sessionId
    ) {
        if (sessionId == null) {
            return null;
        }

        return conversations.get(
                sessionId
        );
    }

    public AgentConversation requireConversation(
            String sessionId
    ) {
        AgentConversation conversation =
                getConversation(sessionId);

        if (conversation == null) {
            throw new IllegalArgumentException(
                    "Session does not exist: "
                            + sessionId
            );
        }

        return conversation;
    }

    public AgentTask createTask(
            String sessionId,
            String task
    ) {
        if (!sessionManager.contains(
                sessionId
        )) {
            throw new IllegalArgumentException(
                    "Session does not exist: "
                            + sessionId
            );
        }

        if (!conversations.containsKey(
                sessionId
        )) {
            conversations.put(
                    sessionId,
                    new AgentConversation(
                            sessionId
                    )
            );
        }

        return taskManager.create(
                sessionId,
                task
        );
    }

    public AgentRequest createRequest(
            AgentTask task
    ) {
        if (task == null) {
            throw new IllegalArgumentException(
                    "Agent task cannot be null"
            );
        }

        return new AgentRequest(
                task.getSessionId(),
                task.getTask(),
                capabilityContext()
        );
    }

    public AgentResponse send(
            AgentTask task
    ) {
        if (task == null) {
            throw new IllegalArgumentException(
                    "Agent task cannot be null"
            );
        }

        AgentConversation conversation =
                requireConversation(
                        task.getSessionId()
                );

        conversation.addUserMessage(
                task.getTask()
        );

        AgentRequest request =
                createRequest(task);

        task.start();

        AgentResponse response =
                transport.send(
                        config,
                        request
                );

        if (response.isSuccessful()) {
            task.complete();

            processResponse(
                    task,
                    conversation,
                    response
            );
        } else {
            task.fail();

            recordFailure(
                    conversation,
                    response
            );
        }

        return response;
    }

    public void sendAsync(
            AgentTask task,
            AgentCallback callback
    ) {
        if (task == null) {
            throw new IllegalArgumentException(
                    "Agent task cannot be null"
            );
        }

        if (callback == null) {
            throw new IllegalArgumentException(
                    "AgentCallback cannot be null"
            );
        }

        AgentConversation conversation =
                requireConversation(
                        task.getSessionId()
                );

        conversation.addUserMessage(
                task.getTask()
        );

        AgentRequest request =
                createRequest(task);

        task.start();

        transport.sendAsync(
                config,
                request,
                new AgentCallback() {

                    @Override
                    public void onSuccess(
                            AgentResponse response
                    ) {
                        task.complete();

                        processResponse(
                                task,
                                conversation,
                                response
                        );

                        callback.onSuccess(
                                response
                        );
                    }

                    @Override
                    public void onFailure(
                            AgentResponse response
                    ) {
                        task.fail();

                        recordFailure(
                                conversation,
                                response
                        );

                        callback.onFailure(
                                response
                        );
                    }
                }
        );
    }

    public AgentResponse execute(
            String sessionId,
            String task
    ) {
        return send(
                createTask(
                        sessionId,
                        task
                )
        );
    }

    public void executeAsync(
            String sessionId,
            String task,
            AgentCallback callback
    ) {
        sendAsync(
                createTask(
                        sessionId,
                        task
                ),
                callback
        );
    }

    public void registerCapability(
            AgentCapability capability,
            ActionSchema actionSchema,
            AgentCapabilityHandler handler
    ) {
        capabilityRegistry.register(
                capability
        );

        capabilityActionRegistry.register(
                capability,
                actionSchema,
                handler
        );

        actionRegistry.register(
                actionSchema,
                handler::handle
        );

        authorizationRegistry.register(
                actionSchema.getName()
        );
    }

    public void unregisterCapability(
            String capabilityName
    ) {
        if (capabilityName == null) {
            return;
        }

        RegisteredCapability registered =
                capabilityActionRegistry.get(
                        capabilityName
                );

        capabilityActionRegistry.unregister(
                capabilityName
        );

        capabilityRegistry.unregister(
                capabilityName
        );

        if (registered != null) {
            String actionName =
                    registered
                            .getActionSchema()
                            .getName();

            authorizationRegistry.unregister(
                    actionName
            );

            permissionRegistry.unregister(
                    actionName
            );

            actionRegistry.unregister(
                    actionName
            );
        }
    }

    public void authorizeAction(
            String actionName
    ) {
        authorizationRegistry.authorize(
                actionName
        );
    }

    public void denyAction(
            String actionName
    ) {
        authorizationRegistry.deny(
                actionName
        );
    }

    public void resetActionAuthorization(
            String actionName
    ) {
        authorizationRegistry.reset(
                actionName
        );
    }

    public void registerActionPermissions(
            String actionName,
            AgentPermission... permissions
    ) {
        if (actionName == null ||
                actionName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "actionName must not be empty"
            );
        }

        permissionRegistry.register(
                actionName,
                permissions
        );
    }

    public void registerCapabilityPermissions(
            String capabilityName,
            AgentPermission... permissions
    ) {
        if (capabilityName == null ||
                capabilityName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "capabilityName must not be empty"
            );
        }

        RegisteredCapability registered =
                capabilityActionRegistry.get(
                        capabilityName
                );

        if (registered == null) {
            throw new IllegalArgumentException(
                    "Capability is not registered: "
                            + capabilityName
            );
        }

        String actionName =
                registered
                        .getActionSchema()
                        .getName();

        permissionRegistry.register(
                actionName,
                permissions
        );
    }

    public void clearActionPermissions(
            String actionName
    ) {
        permissionRegistry.unregister(
                actionName
        );
    }

    public AgentPermissionResult
            checkActionPermissions(
                    String actionName
            ) {
        AgentPermissionRequirement requirement =
                permissionRegistry.get(
                        actionName
                );

        if (requirement == null ||
                !requirement.requiresPermissions()) {

            return AgentPermissionResult.granted();
        }

        if (permissionChecker == null) {
            return AgentPermissionResult.denied(
                    requiredPermissionNames(
                            requirement
                    )
            );
        }

        List<String> missing =
                permissionChecker
                        .getMissingPermissions(
                                requirement
                                        .getPermissions()
                        );

        if (missing.isEmpty()) {
            return AgentPermissionResult.granted();
        }

        return AgentPermissionResult.denied(
                missing
        );
    }

    public CapabilityNegotiationResult
            negotiateCapability(
                    String capabilityName
            ) {
        return capabilityNegotiation.negotiate(
                capabilityName
        );
    }

    public CapabilityNegotiationResult
            negotiateCapability(
                    String capabilityName,
                    Context context
            ) {
        return capabilityNegotiation.negotiate(
                capabilityName,
                context
        );
    }

    public AgentCapabilityDiscovery
            createCapabilityDiscovery() {
        return new AgentCapabilityDiscovery(
                capabilityRegistry,
                capabilityActionRegistry,
                permissionRegistry,
                authorizationRegistry,
                permissionChecker
        );
    }

    public JSONObject discoverCapabilities(
            Context context
    ) {
        return createCapabilityDiscovery()
                .discover(context);
    }

    public List<AgentActionResult> executeActions(
            AgentTask task,
            List<AgentAction> actions
    ) {
        if (task == null) {
            throw new IllegalArgumentException(
                    "Agent task cannot be null"
            );
        }

        if (actions == null ||
                actions.isEmpty()) {

            return Collections.emptyList();
        }

        ArrayList<AgentActionResult> results =
                new ArrayList<>();

        for (AgentAction action : actions) {

            if (action == null) {
                continue;
            }

            task.addAction(action);

            AgentActionResult result =
                    actionExecutor.execute(
                            action
                    );

            task.addActionResult(
                    result
            );

            results.add(
                    result
            );

            recordAction(
                    task.getSessionId(),
                    action,
                    result
            );
        }

        return Collections.unmodifiableList(
                results
        );
    }

    public JSONObject negotiateRemoteCapabilities(
            AgentNegotiationRequest request
    ) {
        JSONObject response =
                new JSONObject();

        try {
            JSONArray capabilities =
                    new JSONArray();

            JSONArray actions =
                    new JSONArray();

            if (request != null) {

                for (String capabilityName :
                        request.getCapabilities()) {

                    CapabilityNegotiationResult result =
                            negotiateCapability(
                                    capabilityName
                            );

                    capabilities.put(
                            result.toJson()
                    );
                }

                for (String actionName :
                        request.getActions()) {

                    JSONObject actionResult =
                            new JSONObject();

                    actionResult.put(
                            "action",
                            actionName
                    );

                    AgentAuthorization.Status
                            authorization =
                            authorizationRegistry
                                    .getStatus(
                                            actionName
                                    );

                    actionResult.put(
                            "authorization",
                            authorization.name()
                    );

                    AgentPermissionResult permissions =
                            checkActionPermissions(
                                    actionName
                            );

                    actionResult.put(
                            "permissionsGranted",
                            permissions.isGranted()
                    );

                    JSONArray missing =
                            new JSONArray();

                    for (String permission :
                            permissions
                                    .getMissingPermissions()) {

                        missing.put(
                                permission
                        );
                    }

                    actionResult.put(
                            "missingPermissions",
                            missing
                    );

                    actions.put(
                            actionResult
                    );
                }
            }

            response.put(
                    "capabilities",
                    capabilities
            );

            response.put(
                    "actions",
                    actions
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to negotiate remote capabilities",
                    exception
            );
        }

        return response;
    }

    public void sendNegotiationResponse(
            AgentTask task,
            AgentNegotiationRequest request,
            JSONObject evaluation
    ) {
        if (task == null) {
            throw new IllegalArgumentException(
                    "Agent task cannot be null"
            );
        }

        if (request == null) {
            throw new IllegalArgumentException(
                    "Negotiation request cannot be null"
            );
        }

        if (evaluation == null) {
            throw new IllegalArgumentException(
                    "Negotiation evaluation cannot be null"
            );
        }

        AgentNegotiationResponse negotiationResponse =
                new AgentNegotiationResponse(
                        request,
                        evaluation
                );

        JSONObject context =
                new JSONObject();

        try {
            context.put(
                    "negotiationResponse",
                    negotiationResponse.toJson()
            );

            context.put(
                    "protocol",
                    "progold"
            );

            context.put(
                    "version",
                    "1.0"
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to create negotiation response request",
                    exception
            );
        }

        AgentRequest followUp =
                new AgentRequest(
                        UUID.randomUUID().toString(),
                        task.getSessionId(),
                        "capability_negotiation_response",
                        context
                );

        AgentConversation conversation =
                getConversation(
                        task.getSessionId()
                );

        if (conversation != null) {

            conversation.addMessage(
                    new AgentMessage(
                            AgentMessage.Role.SYSTEM,
                            negotiationResponse
                                    .toJson()
                                    .toString()
                    )
            );
        }

        transport.sendAsync(
                config,
                followUp,
                new AgentCallback() {

                    @Override
                    public void onSuccess(
                            AgentResponse response
                    ) {
                        if (conversation == null) {
                            return;
                        }

                        String body =
                                response.getBody();

                        if (body != null &&
                                !body.trim().isEmpty()) {

                            conversation.addAgentMessage(
                                    body
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            AgentResponse response
                    ) {
                        recordFailure(
                                conversation,
                                response
                        );
                    }
                }
        );
    }

    private List<String> requiredPermissionNames(
            AgentPermissionRequirement requirement
    ) {
        ArrayList<String> names =
                new ArrayList<>();

        for (AgentPermission permission :
                requirement.getPermissions()) {

            if (permission != null &&
                    permission.isRequired()) {

                names.add(
                        permission.getPermission()
                );
            }
        }

        return Collections.unmodifiableList(
                names
        );
    }

    private JSONObject capabilityContext() {

        JSONObject context =
                new JSONObject();

        try {
            context.put(
                    "capabilityManifest",
                    capabilityManifest.toJson()
            );

            context.put(
                    "authorization",
                    authorizationRegistry.toJson()
            );

            context.put(
                    "permissions",
                    permissionRegistry.toJson()
            );

            JSONArray negotiation =
                    new JSONArray();

            for (String capabilityName :
                    capabilityRegistry.getNames()) {

                CapabilityNegotiationResult result =
                        capabilityNegotiation.negotiate(
                                capabilityName
                        );

                negotiation.put(
                        result.toJson()
                );
            }

            context.put(
                    "negotiation",
                    negotiation
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to create capability context",
                    exception
            );
        }

        return context;
    }

    private void processResponse(
            AgentTask task,
            AgentConversation conversation,
            AgentResponse response
    ) {
        if (response == null) {
            return;
        }

        String body =
                response.getBody();

        if (body == null ||
                body.trim().isEmpty()) {
            return;
        }

        try {
            AgentResponseParser.ParsedResponse parsed =
                    AgentResponseParser.parse(
                            body
                    );

            String message =
                    parsed.getMessage();

            if (message != null &&
                    !message.trim().isEmpty()) {

                conversation.addAgentMessage(
                        message
                );
            }

            if (parsed.requiresNegotiation()) {

                JSONObject negotiationResult =
                        negotiateRemoteCapabilities(
                                parsed.getNegotiation()
                        );

                conversation.addMessage(
                        new AgentMessage(
                                AgentMessage.Role.SYSTEM,
                                negotiationResult.toString()
                        )
                );

                sendNegotiationResponse(
                        task,
                        parsed.getNegotiation(),
                        negotiationResult
                );
            }

            if (parsed.requiresAction()) {

                executeActions(
                        task,
                        parsed.getActions()
                );
            }

        } catch (Exception exception) {

            conversation.addAgentMessage(
                    body
            );
        }
    }

    private void recordAction(
            String sessionId,
            AgentAction action,
            AgentActionResult result
    ) {
        AgentConversation conversation =
                getConversation(
                        sessionId
                );

        if (conversation == null) {
            return;
        }

        conversation.addMessage(
                new AgentMessage(
                        AgentMessage.Role.ACTION,
                        action.toJson().toString()
                )
        );

        conversation.addMessage(
                new AgentMessage(
                        AgentMessage.Role.ACTION_RESULT,
                        result.toJson().toString()
                )
        );
    }

    private void recordFailure(
            AgentConversation conversation,
            AgentResponse response
    ) {
        if (conversation == null ||
                response == null) {
            return;
        }

        String error =
                response.getError();

        if (error == null ||
                error.trim().isEmpty()) {

            error = "Agent request failed";
        }

        conversation.addMessage(
                new AgentMessage(
                        AgentMessage.Role.SYSTEM,
                        error
                )
        );
    }

    public void removeSession(
            String sessionId
    ) {
        if (sessionId == null) {
            return;
        }

        sessionManager.remove(
                sessionId
        );

        conversations.remove(
                sessionId
        );
    }

    public void shutdown() {
        transport.shutdown();
    }
}
