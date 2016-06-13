package com.hectorortega.agent;

import java.util.List;

public interface AgentClient {

    void start(List<Agent> p);

    List<Agent> getState(List<Agent> agents);
}
