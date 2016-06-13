package com.hectorortega.fsm.states;

public class StateProcessingException extends RuntimeException{
    public StateProcessingException(Throwable e) {
        super(e);
    }
}
