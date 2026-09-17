package com.pro.gold;

/**
 * Handles an action requested by an agent.
 *
 * The host application controls what each action is allowed to do.
 */
public interface AgentActionHandler {

    AgentActionResult handle(AgentAction action);
}
