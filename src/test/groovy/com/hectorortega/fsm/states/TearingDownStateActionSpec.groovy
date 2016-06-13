package com.hectorortega.fsm.states

import com.hectorortega.agent.Agent
import com.hectorortega.agent.AgentsDao
import com.hectorortega.aws.EC2Client
import com.hectorortega.aws.EC2InstanceStates
import com.hectorortega.fsm.FSMTask
import spock.lang.Specification


class TearingDownStateActionSpec extends Specification {

    AgentsDao agentsDao = Mock()
    EC2Client ec2Client = Mock()
    def subject = new TearingDownStateAction(agentsDao, ec2Client)
    def task = new FSMTask(1)

    def "process - when all instances are RUNNING, terminate them and return CURRENT"() {
        given:
        def updatedAgents = [new Agent("1", EC2InstanceStates.RUNNING), new Agent("1", EC2InstanceStates.RUNNING)]

        when:
        def nextState = subject.process(task)

        then:
        1 * agentsDao.readAll(1) >> [new Agent("1"), new Agent("2")]
        1 * ec2Client.retrieveAgentMetadata([new Agent("1"), new Agent("2")]) >> updatedAgents
        1 * ec2Client.terminate(updatedAgents)
        nextState == FSMState.TEARING_DOWN
    }

    def "process - when some instances are not RUNNING, update DB and return CURRENT"() {
        given:
        def updatedAgents = [new Agent("1", EC2InstanceStates.SHUTTING_DOWN), new Agent("2", EC2InstanceStates.RUNNING)]
        def storedAgents = [new Agent("1", EC2InstanceStates.RUNNING), new Agent("2", EC2InstanceStates.RUNNING)]

        when:
        def nextState = subject.process(task)


        then:
        1 * agentsDao.readAll(1) >> storedAgents
        1 * ec2Client.retrieveAgentMetadata(storedAgents) >> updatedAgents
        1 * agentsDao.update({ List<Agent> agents ->
            (agents.size() == 1 && agents.get(0).getInstanceId() == "1" && agents.get(0).getInstanceState() == EC2InstanceStates.SHUTTING_DOWN)
        })
        nextState == FSMState.TEARING_DOWN
    }

    def "process - when all instances are TERMINATED, update DB and return FINISHED"() {
        given:
        def updatedAgents = [new Agent("1", EC2InstanceStates.TERMINATED), new Agent("2", EC2InstanceStates.TERMINATED)]
        def storedAgents = [new Agent("1", EC2InstanceStates.SHUTTING_DOWN), new Agent("2", EC2InstanceStates.TERMINATED)]

        when:
        def nextState = subject.process(task)


        then:
        1 * agentsDao.readAll(1) >> storedAgents
        1 * ec2Client.retrieveAgentMetadata(storedAgents) >> updatedAgents
        1 * agentsDao.update({ List<Agent> agents ->
            (agents.size() == 1 && agents.get(0).getInstanceId() == "1" && agents.get(0).getInstanceState() == EC2InstanceStates.TERMINATED)
        })
        nextState == FSMState.FINISHED
    }
}
