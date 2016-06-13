package com.hectorortega.aws;

import com.hectorortega.agent.Agent;

import java.util.List;

public interface EC2Client {

    /**
     * Issues a request to create {agentCount} EC2 instances of type
     * {agentType} and return their instances ids
     * @param agentCount
     * @return
     */
    List<String> create(Integer agentCount);

    /**
     * Returns a List of new Agent objects with their AWS states
     * and public IPs updated based on the instance ids from
     * the Agent list argument
     * @param agents
     * @return
     */
    List<Agent> retrieveAgentMetadata(List<Agent> agents);

    void terminate(List<Agent> agents);
}
