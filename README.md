# Progold SDK

[![](https://jitpack.io/v/ch532/progold.svg)](https://jitpack.io/#ch532/progold)
[![Maven Central](https://img.shields.io/maven-central/v/online.chyke/progold-sdk.svg)](https://central.sonatype.com/artifact/online.chyke/progold-sdk)

Android SDK for connecting applications to agentic AI systems through capabilities, actions, sessions, and multi-turn execution.

Progold is an Android SDK for connecting applications to agentic AI systems.

It provides a structured framework for:

- Agent sessions
- Tasks and multi-turn execution
- Agent actions
- Capability registration and discovery
- Capability negotiation
- Action authorization
- Android permission checks
- Conversations and messages
- HTTP transport
- Execution events
- Device context
- Agent responses and protocol handling

## Requirements

- Android API 24+
- Java 21 for building the SDK
- Android Gradle Plugin 9.1.1
- Gradle 9.7.1

## Maven Coordinates

implementation 'com.pro.gold:progold-sdk:1.0.0'

## Initialize Progold

Progold.initialize(context);

## Create an Agent Configuration

AgentConfig config =
        new AgentConfig.Builder(
                "https://your-agent-endpoint.example"
        )
        .apiKey("YOUR_API_KEY")
        .build();

## Connect to the Agent

Agent agent = Progold.connect(config);

## Create a Session

AgentSession session =
        agent.createSession();

## Register a Capability

AgentCapability capability =
        new AgentCapability(
                "device",
                "Device information"
        );

## Define an Action

ActionSchema actionSchema =
        new ActionSchema(
                "get_device_info",
                "Get device information"
        );

Register the capability and action handler according to the application requirements.

## Authorization

Actions can be explicitly authorized:

agent.authorizeAction(
        "get_device_info"
);

Authorization is separate from Android runtime permission state.

## Permissions

Applications can declare permissions required by an action and allow Progold to evaluate whether those permissions are currently granted.

## Multi-Turn Execution

Progold supports agent workflows in which an agent requests an action, the Android application executes the action, and the result is returned to the agent for continued processing.

Example flow:

Application
    |
    v
Progold Agent
    |
    v
Agent Request
    |
    v
Remote Agent
    |
    v
Action Request
    |
    v
Capability / Action Executor
    |
    v
Action Result
    |
    v
Remote Agent
    |
    v
Completed Response

## Capability Negotiation

Applications can expose capabilities and their associated actions so an agent can negotiate what functionality is available.

Progold supports:

- Capability discovery
- Capability manifests
- Capability states
- Action authorization
- Android permission requirements
- Negotiation requests
- Negotiation responses

## HTTP Transport

The default transport uses Android-compatible HTTP networking through Java's HTTP APIs.

Applications can use synchronous or asynchronous agent communication through the Progold transport layer.

## Events

Execution events can be observed through the Progold event system, including task, response, action, and follow-up execution events.

## Android Permissions

Progold does not automatically grant Android permissions.

The host application remains responsible for declaring and requesting Android runtime permissions.

Progold can evaluate the current permission state when executing permission-dependent actions.

## Maven Publication

The SDK is published with:

- Android AAR
- Sources JAR
- Maven POM
- Gradle Module Metadata
- SHA-1 checksum
- SHA-256 checksum
- SHA-512 checksum
- MD5 checksum

## Artifact

Group:
com.pro.gold

Artifact:
progold-sdk

Version:
1.0.0

## License

See LICENSE.


