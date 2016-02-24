package com.atompacman.toolkat.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.StopWatch;

import com.google.common.collect.ImmutableList;

public final class Task {

    //
    //  ~  INNER TYPES  ~  //
    //

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Description {
        String nameFormat();
    }
    
    
    //
    //  ~  FIELDS  ~  //
    //

    private final String            name;
    private final Description       desc;
    private final Optional<Task>    parent;
    private final List<Observation> obs;
    private final List<Task>        subtasks;
    private final StopWatch         time;
    
    
    //
    //  ~  INIT  ~  //
    //

    Task(Description desc, Object... procNameArgs) {
        this(desc, null, procNameArgs);
    }

    private Task(Description desc, Optional<Task> parent, Object...procNameArgs) {
        this.name     = String.format(desc.nameFormat(), procNameArgs);
        this.desc     = desc;
        this.parent   = parent;
        this.obs      = new LinkedList<>();
        this.subtasks = new LinkedList<>();
        this.time     = new StopWatch();
                
        // Start 
        time.start();
    }

    Task createSubTask(Description desc, Object...procNameArgs) {
        // Stop current subtask
        if (!subtasks.isEmpty()) {
            subtasks.get(subtasks.size() - 1).stop();
        }
        
        // Create new subtask
        Task child = new Task(desc, this, procNameArgs);

        // Add to subtasks list
        subtasks.add(child);
        
        return child;
    }
    

    //
    //  ~  SETTERS  ~  //
    //

    void addObservation(Observation ob) {
        obs.add(ob);
    }

    void stop() {
        time.stop();
        if (!subtasks.isEmpty()) {
            subtasks.get(subtasks.size() - 1).time.stop();
        }
    }
    

    //
    //  ~  GETTERS  ~  //
    //

    public String getName() {
        return name;
    }

    public Description getDescription() {
        return desc;
    }

    public Optional<Task> getParentTask() {
        return parent;
    }

    public ImmutableList<Observation> getObservations() {
        return ImmutableList.copyOf(obs);
    }

    public ImmutableList<Task> getSubTasks() {
        return ImmutableList.copyOf(subtasks);
    }

    public int getGeneration() {
        return parent.isPresent() ? parent.get().getGeneration() + 1 : 0;
    }

    public Task getBaseTask() {
        return parent.isPresent() ? parent.get().getBaseTask() : this;
    }

    public Task getCurrentSubtask() {
        return subtasks.isEmpty() ? this : subtasks.get(subtasks.size() - 1).getCurrentSubtask();
    }
    
    
    //
    //  ~  SERIALIZATION  ~  //
    //

    @Override
    public String toString() {
        return name;
    }
}
