package com.atompacman.toolkat.task;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.EnumUtils;
import com.google.common.collect.ImmutableList;

public final class TaskLogger {

    //
    //  ~  FIELDS  ~  //
    //

    private final List<Task> completedTask;

    private Task  currTask;
    private Level verbLvl;

    
    //
    //  ~  INIT  ~  //
    //

    public TaskLogger(Enum<?> task, Object...taskNameArgs) {
        this(task, Level.INFO, taskNameArgs);
    }
    
    public TaskLogger(Enum<?> task, Level verbLvl, Object...taskNameArgs) {
        this.completedTask = new LinkedList<>();

        this.currTask = null;
        this.verbLvl  = verbLvl;
        
        // Start first task
        startTask(task, Optional.empty(), taskNameArgs);
    }

    
    //
    //  ~  START TASKS/SUBTASKS  ~  //
    //

    public void startTask(Enum<?> task, Object...taskNameArgs) {
        // End current task
        currTask.stop();
        completedTask.add(currTask);
        
        // Start task
        startTask(task, Optional.empty(), taskNameArgs);
    }

    public void startSubtask(Enum<?> subtask, Object...taskNameArgs) {
        startTask(subtask, Optional.of(currTask.getCurrentSubtask()), taskNameArgs);
    }

    public void startSubtaskOf(Enum<?> parentTask, Enum<?> subtask, Object...taskNameArgs) {
        // Extract parent task description annotations
        Task.Description parentDesc=EnumUtils.extractAnnotation(parentTask, Task.Description.class);
        
        Optional<Task> parent = Optional.of(currTask);
        while (parent.get().getDescription() != parentDesc) {
            parent = parent.get().getParentTask();
            checkArgument(parent.isPresent(), "Task \"%s\" has no subtask \"%s\"", 
                    currTask, String.format(parentDesc.nameFormat(), taskNameArgs));
        }
        startTask(subtask, parent, taskNameArgs);
    }

    private void startTask(Enum<?>        subtask,
                           Optional<Task> parentTask, 
                           Object...      taskNameArgs) {

        // Extract task description annotation
        Task.Description desc = EnumUtils.extractAnnotation(subtask, Task.Description.class);
        
        // Create subtask if possible
        Task subtsk = null;
        if (parentTask.isPresent()) {
            subtsk = parentTask.get().createSubTask(desc, taskNameArgs);
        } else {
            subtsk = new Task(desc, taskNameArgs);
            currTask = subtsk;
        }

        // Log event
        log(2, subtsk.getName());
    }


    //
    //  ~  LOG  ~  //
    //

    public void log(String format, Object...args) {
        currTask.addObservation(new Observation(String.format(format, args), 
                                                currTask.getGeneration(), 
                                                verbLvl, 
                                                1));
    }

    public void log(int stackTraceMod, String format, Object...args) {
        currTask.addObservation(new Observation(String.format(format, args), 
                                                currTask.getGeneration(), 
                                                verbLvl, 
                                                stackTraceMod + 1));
    }

    public void log(Level verbLvl, String format, Object...args) {
        currTask.addObservation(new Observation(String.format(format, args), 
                                                currTask.getGeneration(), 
                                                verbLvl, 
                                                1));
    }

    public void log(Level verbose, int stackTraceMod, String format, Object...args) {
        currTask.addObservation(new Observation(String.format(format, args), 
                                                currTask.getGeneration(), 
                                                verbLvl, 
                                                stackTraceMod + 1));
    }
    
    
    //
    //  ~  ANOMALIES  ~  //
    //
    
    public void signal(Enum<?> anomaly, Object...args) {
        currTask.addObservation(Anomaly.of(anomaly, currTask.getGeneration(), 1));
    }

    public <T extends Exception> void signalException(Enum<?>   anomaly, 
                                                      Class<T>  excepClass, 
                                                      Object... args) throws T {

        Anomaly ano = Anomaly.of(anomaly, currTask.getGeneration(), 1);
        currTask.addObservation(ano);
        try {
            throw excepClass.getConstructor(String.class).newInstance(ano.getMessage());
        } catch (InstantiationException    | IllegalAccessException | IllegalArgumentException | 
                 InvocationTargetException | NoSuchMethodException  | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    public <T extends Exception> void signalException(Enum<?>   anomaly, 
                                                      Throwable cause,
                                                      Class<T>  excepClass, 
                                                      Object... args) throws T {

        Anomaly ano = Anomaly.of(anomaly, currTask.getGeneration(), 1);
        currTask.addObservation(ano);
        try {
            throw excepClass.getConstructor(String.class, Throwable.class).
                newInstance(ano.getMessage(), cause);
        } catch (InstantiationException    | IllegalAccessException | IllegalArgumentException | 
                InvocationTargetException | NoSuchMethodException  | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    //
    //  ~  SETTERS  ~  //
    //

    public void setVerboseLevel(Level verbLvl) {
        this.verbLvl = verbLvl;
    }
    
    
    //
    //  ~  GETTERS  ~  //
    //

    public ImmutableList<Task> getCompletedTasks() {
        return ImmutableList.copyOf(completedTask);
    }
}
