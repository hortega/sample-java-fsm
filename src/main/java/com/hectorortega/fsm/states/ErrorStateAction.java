package com.hectorortega.fsm.states;

import com.hectorortega.fsm.FSMTask;

import javax.inject.Named;

@Named
public class ErrorStateAction implements FSMStateAction {
    @Override
    public FSMState process(FSMTask task) {
        return FSMState.ERROR;
    }
}
