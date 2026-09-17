package com.pro.gold;

public interface AgentCallback {

    void onSuccess(
            AgentResponse response
    );

    void onFailure(
            AgentResponse response
    );
}
