package com.atompacman.toolkat;

import java.util.Optional;

import com.atompacman.toolkat.task.TaskLogger;

// TODO REMOVE
public class Builder<T> {
    
    protected TaskLogger taskLogger;
    
    protected Builder(Optional<TaskLogger> taskLogger) {
    }
    
    public T build() {
        return null;
    }
}
