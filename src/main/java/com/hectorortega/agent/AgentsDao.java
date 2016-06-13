package com.hectorortega.agent;

import java.util.List;

public interface AgentsDao {

    List<Agent> readAll(Integer lifecycleId);

    void create(Integer lifecycleId, List<String> instanceIds);

    void update(List<Agent> updatedAgents);
}
