package com.hectorortega.testconfiguration;

import com.hectorortega.fsm.states.FSMState;

public class TestModel {

    private Integer id;
    private FSMState currentState;
    private Integer agentCount;

    public TestModel(Integer id) {
        this.id = id;
    }

    public TestModel(Integer id, FSMState currentState, Integer agentCount) {
        this.id = id;
        this.currentState = currentState;
        this.agentCount = agentCount;
    }

    public FSMState getCurrentState() {

        return currentState;
    }

    public void setCurrentState(FSMState currentState) {
        this.currentState = currentState;
    }

    public Integer getAgentCount() {

        return agentCount;
    }

    public void setAgentCount(Integer agentCount) {
        this.agentCount = agentCount;
    }

    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestModel testModel = (TestModel) o;

        if (id != null ? !id.equals(testModel.id) : testModel.id != null) return false;
        if (currentState != testModel.currentState) return false;
        return agentCount != null ? agentCount.equals(testModel.agentCount) : testModel.agentCount == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (currentState != null ? currentState.hashCode() : 0);
        result = 31 * result + (agentCount != null ? agentCount.hashCode() : 0);
        return result;
    }
}
