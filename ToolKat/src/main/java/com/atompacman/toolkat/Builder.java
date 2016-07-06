package com.atompacman.toolkat;

import com.atompacman.toolkat.task.TaskMonitor;

public abstract class Builder<T> {

    //
    //  ~  BUILD  ~  //
    //

    protected abstract T buildImpl(TaskMonitor monitor);

    public abstract void reset();

    public final T build() {
        return build(TaskMonitor.of("Build"));
    }
    
    public final T build(TaskMonitor monitor) {
        T t = buildImpl(monitor);
        reset();
        return t;
    }
}
