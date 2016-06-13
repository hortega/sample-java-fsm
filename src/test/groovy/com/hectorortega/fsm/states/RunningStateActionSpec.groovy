package com.hectorortega.fsm.states

import com.hectorortega.agent.Agent
import com.hectorortega.agent.AgentClient
import com.hectorortega.agent.AgentStateEnum
import com.hectorortega.agent.AgentsDao
import com.hectorortega.fsm.FSMTask
import spock.lang.Specification


class RunningStateActionSpec extends Specification {

    AgentClient agentClient = Mock()
    AgentsDao agentsDao = Mock()
    def subject = new RunningStateAction(agentClient, agentsDao)
    def task = new FSMTask(1)



    def "process - agents are still running, update DB and return CURRENT state"() {
        when:
        def nextState = subject.process(task)

        then:
        1 * agentsDao.readAll(1) >> [new Agent("1", AgentStateEnum.RUNNING), new Agent("2", AgentStateEnum.RUNNING)]
        1 * agentClient.getState([new Agent("1", AgentStateEnum.RUNNING), new Agent("2", AgentStateEnum.RUNNING)]) >> [new Agent("1", AgentStateEnum.STOPPED), new Agent("2", AgentStateEnum.RUNNING)]
        1 * agentsDao.update({ List<Agent> agents ->
            (agents.size() == 1 && agents.get(0).getInstanceId() == "1")
        })
        nextState == FSMState.RUNNING
    }

    def "process - all agents are finished, return NEXT state"() {
        when:
        def nextState = subject.process(task)

        then:
        1 * agentsDao.readAll(1) >> [new Agent("1"), new Agent("2")]
        1 * agentClient.getState([new Agent("1"), new Agent("2")]) >> [new Agent("1", AgentStateEnum.STOPPED), new Agent("1", AgentStateEnum.STOPPED)]
        nextState == FSMState.TEARING_DOWN
    }
}
