package com.hectorortega.fsm.states;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Optional;

@Named
public class StateActionStore {

    private final HashMap<FSMState, FSMStateAction> statesMap = new HashMap<>();
    private ReadyStateAction readyState;
    private InitializingStateAction initializingState;
    private RunningStateAction runningState;
    private TearingDownStateAction tearingDownState;
    private FinishedStateAction finishedState;

    @Inject
    public StateActionStore(ReadyStateAction readyState, InitializingStateAction initializingState, RunningStateAction runningState, TearingDownStateAction tearingDownState, FinishedStateAction finishedState) {
        this.readyState = readyState;
        this.initializingState = initializingState;
        this.runningState = runningState;
        this.tearingDownState = tearingDownState;
        this.finishedState = finishedState;
    }

    @PostConstruct
    public void postConstruct() {
        statesMap.put(FSMState.READY, readyState);
        statesMap.put(FSMState.INITIALIZING, initializingState);
        statesMap.put(FSMState.RUNNING, runningState);
        statesMap.put(FSMState.TEARING_DOWN, tearingDownState);
        statesMap.put(FSMState.FINISHED, finishedState);
    }

    public Optional<FSMStateAction> getState(FSMState stateEnum) {
        FSMStateAction fsmStateAction = statesMap.get(stateEnum);
        return fsmStateAction == null ? Optional.empty() : Optional.of(fsmStateAction);
    }
}
