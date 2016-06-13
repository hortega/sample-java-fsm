package com.hectorortega.fsm.states;

import com.hectorortega.fsm.*;
import com.hectorortega.agent.Agent;
import com.hectorortega.agent.AgentsDao;
import com.hectorortega.aws.EC2Client;
import com.hectorortega.aws.EC2InstanceStates;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
public class TearingDownStateAction implements FSMStateAction {

    public static final FSMState CURRENT_STATE = FSMState.TEARING_DOWN;
    public static final FSMState NEXT_STATE = FSMState.FINISHED;
    private AgentsDao agentsDao;
    private EC2Client ec2Client;

    @Inject
    public TearingDownStateAction(AgentsDao agentsDao, EC2Client ec2Client) {
        this.agentsDao = agentsDao;
        this.ec2Client = ec2Client;
    }

    @Override
    public FSMState process(FSMTask task) {
        List<Agent> agents = agentsDao.readAll(task.getTestId());
        List<Agent> updatedAgents = ec2Client.retrieveAgentMetadata(agents);
        if (allInstancesAreRunning(updatedAgents)) {
            ec2Client.terminate(updatedAgents);
            return CURRENT_STATE;
        } else if (allInstancesAreTerminated(updatedAgents)) {
            updateDb(agents, updatedAgents);
            return NEXT_STATE;
        } else {
            updateDb(agents, updatedAgents);
            return CURRENT_STATE;
        }
    }

    private void updateDb(List<Agent> agents, List<Agent> updatedAgents) {
        List<Agent> agentsToUpdateDB = FSMStateAction.getRightDifference(agents, updatedAgents);
        agentsDao.update(agentsToUpdateDB);
    }

    private boolean allInstancesAreTerminated(List<Agent> agents) {
        return agents.stream().allMatch(agent -> agent.getInstanceState() == EC2InstanceStates.TERMINATED);
    }

    private boolean allInstancesAreRunning(List<Agent> agents) {
        return agents.stream().allMatch(agent -> agent.getInstanceState() == EC2InstanceStates.RUNNING);
    }
}
