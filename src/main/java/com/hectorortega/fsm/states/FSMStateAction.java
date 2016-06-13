package com.hectorortega.fsm.states;

import com.hectorortega.agent.Agent;
import com.hectorortega.fsm.FSMTask;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public interface FSMStateAction {

    static List<Agent> getRightDifference(List<Agent> left, List<Agent> right) {
        List<Agent> difference = new ArrayList<>();
        IntStream.range(0, right.size()).forEach(i -> {
            if (!left.contains(right.get(i))) {
                difference.add(right.get(i));
            }
        });
        return difference;
    }


    /**
     * Receives a task and returns the state the FSM is in
     * after the execution of this method
     * @return
     */
    FSMState process(FSMTask task);

}
