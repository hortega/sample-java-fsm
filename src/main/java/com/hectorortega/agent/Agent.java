package com.hectorortega.agent;

import com.hectorortega.aws.EC2InstanceStates;

public class Agent {
    private String instanceId;
    private EC2InstanceStates instanceState;
    private AgentStateEnum agentState;

    public AgentStateEnum getAgentState() {
        return agentState;
    }

    public void setAgentState(AgentStateEnum agentState) {
        this.agentState = agentState;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    private String publicIp;

    public Agent(String instanceId, AgentStateEnum agentState) {
        this.instanceId = instanceId;
        this.agentState = agentState;
    }

    public Agent(String instanceId, EC2InstanceStates instanceState) {
        this.instanceId = instanceId;
        this.instanceState = instanceState;
    }

    public Agent(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public EC2InstanceStates getInstanceState() {
        return instanceState;
    }

    public void setInstanceState(EC2InstanceStates instanceState) {
        this.instanceState = instanceState;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Agent agent = (Agent) o;

        if (instanceId != null ? !instanceId.equals(agent.instanceId) : agent.instanceId != null) return false;
        if (instanceState != agent.instanceState) return false;
        if (agentState != agent.agentState) return false;
        return publicIp != null ? publicIp.equals(agent.publicIp) : agent.publicIp == null;

    }

    @Override
    public int hashCode() {
        int result = instanceId != null ? instanceId.hashCode() : 0;
        result = 31 * result + (instanceState != null ? instanceState.hashCode() : 0);
        result = 31 * result + (agentState != null ? agentState.hashCode() : 0);
        result = 31 * result + (publicIp != null ? publicIp.hashCode() : 0);
        return result;
    }
}
