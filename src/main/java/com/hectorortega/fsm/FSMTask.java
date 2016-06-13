package com.hectorortega.fsm;

import com.hectorortega.fsm.states.FSMState;

public class FSMTask {

    private Integer testId;
    private FSMState nextState;

    public FSMTask(FSMTask task, FSMState newState) {
        this.testId = task.getTestId();
        this.nextState = newState;
    }

    public FSMState getNextState() {
        return nextState;
    }

    public void setNextState(FSMState nextState) {
        this.nextState = nextState;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }


    public FSMTask(Integer testId) {
        this.testId = testId;
    }

    public Integer getTestId() {

        return testId;
    }

}
