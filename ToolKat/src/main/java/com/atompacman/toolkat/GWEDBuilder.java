package com.atompacman.toolkat;

import java.util.Optional;

import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.task.TaskLogger;

public abstract class GWEDBuilder<T> {

    //
    //  ~  FIELDS  ~  //
    //

    protected TaskLogger taskLogger;


    //
    //  ~  INIT  ~  //
    //

    protected Builder(Optional<TaskLogger> taskLogger) {
        this.taskLogger = taskLogger.isPresent() ? taskLogger.get() : TaskLogger.of();
        this.taskLogger.setVerboseLevel(Level.TRACE);
    }


    //
    //  ~  BUILD  ~  //
    //

    protected abstract T buildImpl();

    public abstract void reset();

    public final T build() {
        T t = buildImpl();
        reset();
        return t;
    }
}
