package com.fxguild.common;

import com.fxguild.common.task.TaskMonitor;

/**
 * Skeleton for classes that implements a monitored builder design pattern.
 * 
 * @param <T> The type that is built
 */
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
