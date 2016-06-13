package com.hectorortega.fsm.states

import com.hectorortega.agent.Agent
import com.hectorortega.agent.AgentClient
import com.hectorortega.agent.AgentStateEnum
import com.hectorortega.agent.AgentsDao
import com.hectorortega.aws.EC2Client
import com.hectorortega.aws.EC2ClientException
import com.hectorortega.aws.EC2InstanceStates
import com.hectorortega.fsm.FSMTask
import com.hectorortega.testconfiguration.TestDao
import com.hectorortega.testconfiguration.TestModel
import spock.lang.Specification


class InitializingStateActionSpec extends Specification {

    AgentsDao agentsDao = Mock()
    EC2Client ec2Client = Mock()
    AgentClient agentClient = Mock()
    TestDao testDao = Mock()
    def subject = new InitializingStateAction(agentsDao, ec2Client, agentClient, testDao)
    def task = new FSMTask(1)

    def "process - when there are no agents in DB, create the instances and return CURRENT"() {
        when:
        def nextState = subject.process(task)

        then:
        1 * testDao.get(new TestModel(1)) >> Optional.of(new TestModel(1, FSMState.READY, 2))
        1 * agentsDao.readAll(1) >> []
        1 * ec2Client.create(2) >> ["i-1", "i-2"]
        nextState == FSMState.INITIALIZING
    }

    def "process - when instances are being created but are not running return CURRENT"() {
        when:
        def nextState = subject.process(task)

        then:
        1 * agentsDao.readAll(1) >> [new Agent("1"), new Agent("2")]
        1 * ec2Client.retrieveAgentMetadata(_ as List) >> [new Agent("1", EC2InstanceStates.PENDING), new Agent("1", EC2InstanceStates.RUNNING)]
        nextState == FSMState.INITIALIZING
    }

    def "process - when all instances are running and some agents are UNRESPONSIVE, return CURRENT state"() {
        given:
        def updatedAgents = [new Agent("1", EC2InstanceStates.RUNNING), new Agent("1", EC2InstanceStates.RUNNING)]
        when:
        def nextState = subject.process(task)

        then:
        1 * agentsDao.readAll(1) >> [new Agent("1"), new Agent("2")]
        1 * ec2Client.retrieveAgentMetadata(_ as List) >> updatedAgents
        1 * agentClient.getState(_ as List) >> [new Agent("1", AgentStateEnum.UNRESPONSIVE), new Agent("1", AgentStateEnum.STOPPED)]
        nextState == FSMState.INITIALIZING
    }

    def "process - when all instances are running and agents are STOPPED, start the agents and return NEXT state"() {
        given:
        def updatedAgents = [new Agent("1", EC2InstanceStates.RUNNING), new Agent("1", EC2InstanceStates.RUNNING)]
        when:
        def nextState = subject.process(task)

        then:
        1 * agentsDao.readAll(1) >> [new Agent("1"), new Agent("2")]
        1 * ec2Client.retrieveAgentMetadata(_ as List) >> updatedAgents
        1 * agentClient.getState(_ as List) >> [new Agent("1", AgentStateEnum.STOPPED), new Agent("1", AgentStateEnum.STOPPED)]
        1 * agentClient.start(updatedAgents)
        nextState == FSMState.RUNNING
    }

    def "process - exceptions get rethrown as StateProcessingException"() {
        when:
        subject.process(task)

        then:
        1 * agentsDao.readAll(1) >> [new Agent("1"), new Agent("2")]
        1 * ec2Client.retrieveAgentMetadata(_ as List) >> { throw new EC2ClientException("msg")}
        StateProcessingException e = thrown()
        e.getCause().getMessage() == "msg"
    }



}
