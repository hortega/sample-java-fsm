package com.hectorortega.fsm

import com.hectorortega.testconfiguration.TestDao
import com.hectorortega.testconfiguration.TestModel
import com.hectorortega.fsm.states.FSMStateAction
import com.hectorortega.fsm.states.FSMState
import com.hectorortega.fsm.states.StateActionStore
import com.hectorortega.fsm.states.StateProcessingException
import spock.lang.Specification

class FSMContextSpec extends Specification {

    def task = new FSMTask(1)
    StateActionStore statesStore = Mock()
    TestDao testDao = Mock()
    FSMContext subject = new FSMContext(task, statesStore, testDao)

    FSMStateAction mockState = Mock()

    def "call - transition success"() {
        when:
        def newTask = subject.call()

        then:
        1 * testDao.get(new TestModel(1)) >> Optional.of(new TestModel(1, currentState, 2))
        1 * statesStore.getState(currentState) >> Optional.of(mockState)
        1 * mockState.process(task) >> nextState
        newTask.getNextState() == nextState

        where:
        currentState | nextState
        FSMState.READY | FSMState.INITIALIZING
        FSMState.INITIALIZING | FSMState.RUNNING
        FSMState.RUNNING | FSMState.TEARING_DOWN
        FSMState.TEARING_DOWN | FSMState.FINISHED
        FSMState.FINISHED | FSMState.FINISHED
        FSMState.RUNNING | FSMState.ERROR

    }

    def "call - StateProcessingException puts the test in Error state"() {
        when:
        def newTask = subject.call()

        then:
        1 * testDao.get(new TestModel(1)) >> Optional.of(new TestModel(1, FSMState.INITIALIZING, 2))
        1 * statesStore.getState(FSMState.INITIALIZING) >> Optional.of(mockState)
        1 * mockState.process(task) >> {throw new StateProcessingException()}
        newTask.getNextState() == FSMState.ERROR
    }
}
