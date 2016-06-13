package com.hectorortega.fsm;

import com.hectorortega.testconfiguration.TestDao;
import com.hectorortega.testconfiguration.TestModel;
import com.hectorortega.fsm.states.*;

import java.util.concurrent.Callable;

public class FSMContext implements Callable<FSMTask> {

    private FSMTask task;
    private StateActionStore stateActionStore;
    private TestDao testDao;

    public FSMContext(FSMTask task, StateActionStore stateActionStore, TestDao testDao) {
        this.task = task;
        this.stateActionStore = stateActionStore;
        this.testDao = testDao;
    }


    @Override
    public FSMTask call() throws Exception {
        TestModel testModel = testDao.get(new TestModel(task.getTestId())).get();
        FSMStateAction state = stateActionStore.getState(testModel.getCurrentState()).get();
        try {
            FSMState newState = state.process(task);
            return new FSMTask(task, newState);
        } catch (StateProcessingException e) {
            // log the exception
            return new FSMTask(task, FSMState.ERROR);
        }
    }

}
