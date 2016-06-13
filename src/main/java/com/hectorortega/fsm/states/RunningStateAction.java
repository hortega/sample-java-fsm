package com.hectorortega.fsm.states;

import com.hectorortega.fsm.*;
import com.hectorortega.agent.Agent;
import com.hectorortega.agent.AgentClient;
import com.hectorortega.agent.AgentStateEnum;
import com.hectorortega.agent.AgentsDao;

import javax.inject.Named;
import java.util.List;

@Named
public class RunningStateAction implements FSMStateAction {

    public static final FSMState CURRENT_STATE = FSMState.TEARING_DOWN;
    public static final FSMState NEXT_STATE = FSMState.RUNNING;
    private AgentClient agentClient;
    private AgentsDao agentsDao;

    public RunningStateAction(AgentClient agentClient, AgentsDao agentsDao) {
        this.agentClient = agentClient;
        this.agentsDao = agentsDao;
    }

    @Override
    public FSMState process(FSMTask task) {
        List<Agent> agents = agentsDao.readAll(task.getTestId());
        List<Agent> updatedAgents = agentClient.getState(agents);
        updateDb(agents, updatedAgents);
        if(allAgentsAreStopped(updatedAgents)){
            return CURRENT_STATE;
        }
        return NEXT_STATE;

    }

    private void updateDb(List<Agent> agents, List<Agent> updatedAgents) {
        List<Agent> agentsToUpdateDB = FSMStateAction.getRightDifference(agents, updatedAgents);
        agentsDao.update(agentsToUpdateDB);
    }
    private boolean allAgentsAreStopped(List<Agent> agents) {
        return agents.stream().allMatch(agent -> agent.getAgentState() == AgentStateEnum.STOPPED);
    }
}
