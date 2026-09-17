package com.pro.gold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AgentTaskManager {

    private final Map<String, AgentTask> tasks =
            new ConcurrentHashMap<>();

    public AgentTask create(
            String sessionId,
            String task
    ) {

        AgentTask agentTask =
                new AgentTask(
                        sessionId,
                        task
                );

        tasks.put(
                agentTask.getTaskId(),
                agentTask
        );

        return agentTask;
    }

    public AgentTask get(
            String taskId
    ) {

        if (taskId == null) {
            return null;
        }

        return tasks.get(taskId);
    }

    public AgentTask remove(
            String taskId
    ) {

        if (taskId == null) {
            return null;
        }

        return tasks.remove(taskId);
    }

    public boolean contains(
            String taskId
    ) {

        if (taskId == null) {
            return false;
        }

        return tasks.containsKey(taskId);
    }

    public int size() {
        return tasks.size();
    }

    public List<String> getTaskIds() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        tasks.keySet()
                )
        );
    }

    public List<AgentTask> getTasksForSession(
            String sessionId
    ) {

        if (sessionId == null) {
            return Collections.emptyList();
        }

        List<AgentTask> result =
                new ArrayList<>();

        for (AgentTask task : tasks.values()) {

            if (sessionId.equals(
                    task.getSessionId())) {

                result.add(task);
            }
        }

        return Collections.unmodifiableList(
                result
        );
    }

    public void clear() {
        tasks.clear();
    }
}
