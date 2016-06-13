package com.hectorortega.fsm.states;

import com.hectorortega.testconfiguration.TestDao;
import com.hectorortega.testconfiguration.TestModel;
import com.hectorortega.fsm.*;
import com.hectorortega.agent.Agent;
import com.hectorortega.agent.AgentClient;
import com.hectorortega.agent.AgentStateEnum;
import com.hectorortega.agent.AgentsDao;
import com.hectorortega.aws.EC2Client;
import com.hectorortega.aws.EC2InstanceStates;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;


@Named
public class InitializingStateAction implements FSMStateAction {

    public static final FSMState CURRENT_STATE = FSMState.INITIALIZING;
    public static final FSMState NEXT_STATE = FSMState.RUNNING;

    private AgentsDao agentsDao;
    private EC2Client ec2Client;
    private AgentClient agentClient;
    private TestDao testDao;

    @Inject
    public InitializingStateAction(AgentsDao agentsDao, EC2Client ec2Client, AgentClient agentClient, TestDao testDao) {
        this.agentsDao = agentsDao;
        this.ec2Client = ec2Client;
        this.agentClient = agentClient;
        this.testDao = testDao;
    }

    @Override
    public FSMState process(FSMTask task) {
        try {
            List<Agent> agents = agentsDao.readAll(task.getTestId());
            if (agents.size() == 0) {
                TestModel testModel = testDao.get(new TestModel(task.getTestId())).get();
                List<String> instanceIds = ec2Client.create(testModel.getAgentCount());
                agentsDao.create(task.getTestId(), instanceIds);
                return CURRENT_STATE;
            } else {
                List<Agent> updatedAgents = ec2Client.retrieveAgentMetadata(agents);
                agentsDao.update(updatedAgents);
                if (allInstancesAreRunning(updatedAgents) && allAgentsAreStopped(updatedAgents)) {
                    agentClient.start(updatedAgents);
                    return NEXT_STATE;
                }
                return CURRENT_STATE;
            }
        } catch (Exception e) {
            throw new StateProcessingException(e);
        }
    }

    private boolean allAgentsAreStopped(List<Agent> updatedAgents) {
        List<Agent> agents = agentClient.getState(updatedAgents);
        return agents.stream().allMatch(agent -> agent.getAgentState() == AgentStateEnum.STOPPED);
    }

    private boolean allInstancesAreRunning(List<Agent> updatedAgents) {
        return updatedAgents.stream().allMatch(agent -> agent.getInstanceState() == EC2InstanceStates.RUNNING);
    }
}
