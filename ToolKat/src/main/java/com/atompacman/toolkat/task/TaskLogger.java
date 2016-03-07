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

    private Optional<Task>  currTask;
    private Level 			verbLvl;

    
    //
    //  ~  INIT  ~  //
    //

    public TaskLogger() {
        this(Level.INFO);
    }
    
    public TaskLogger(Level verbLvl) {
        this.completedTask = new LinkedList<>();

        this.currTask = Optional.empty();
        this.verbLvl  = verbLvl;
    }

    
    //
    //  ~  START TASKS/SUBTASKS  ~  //
    //

    public void startTask(Enum<?> task, Object...taskNameArgs) {
        // End current task
    	if (currTask.isPresent()) {
            currTask.get().stop();
            completedTask.add(currTask.get());
    	}
        
        // Start task
        startTaskImpl(task, Optional.empty(), taskNameArgs);
    }

    public void startSubtask(Enum<?> subtask, Object...taskNameArgs) {
    	checkArgument(currTask.isPresent(), "Cannot start subtask when no task is started");
        startTaskImpl(subtask, Optional.of(currTask.get().getCurrentSubtask()), taskNameArgs);
    }

    public void startSubtaskOf(Enum<?> parentTask, Enum<?> subtask, Object...taskNameArgs) {
    	checkArgument(currTask.isPresent(), "Cannot start subtask when no task is started");
    	
        // Extract parent task description annotations
        Task.Description parentDesc=EnumUtils.extractAnnotation(parentTask, Task.Description.class);
        
        Optional<Task> parent = Optional.of(currTask.get());
        while (parent.get().getDescription() != parentDesc) {
            parent = parent.get().getParentTask();
            checkArgument(parent.isPresent(), "Task \"%s\" has no subtask \"%s\"", 
                    currTask, String.format(parentDesc.nameFormat(), taskNameArgs));
        }
        startTaskImpl(subtask, parent, taskNameArgs);
    }

    private void startTaskImpl(Enum<?>        subtask,
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
            currTask = Optional.of(subtsk);
        }

        // Log event
        log(2, subtsk.getName());
    }


    //
    //  ~  LOG  ~  //
    //

    public void log(String format, Object...args) {
    	logImpl(verbLvl, 1, format, args);
    }

    public void log(int stackTraceMod, String format, Object...args) {
    	logImpl(verbLvl, stackTraceMod + 1, format, args);
    }

    public void log(Level verbLvl, String format, Object...args) {
    	logImpl(verbLvl, 1, format, args);

    }

    public void log(Level verbose, int stackTraceMod, String format, Object...args) {
    	logImpl(verbose, stackTraceMod + 1, format, args);
    }
    
    private void logImpl(Level verbose, int stackTraceMod, String format, Object...args) {
    	checkArgument(currTask.isPresent(), "Cannot log when no task is started");
    	String msg = String.format(format, args);
    	int gen = currTask.get().getGeneration();
        currTask.get().addObservation(new Observation(msg, gen, verbLvl, stackTraceMod + 1));
    }
    
    
    //
    //  ~  ANOMALIES  ~  //
    //
    
    public void signal(Enum<?> anomaly, Object...args) {
    	signalImpl(anomaly, 1, args);
    }

    public <T extends Exception> void signalException(Enum<?>   anomaly, 
                                                      Class<T>  excepClass, 
                                                      Object... args) throws T {

    	String msg = signalImpl(anomaly, 1, args);
        try {
            throw excepClass.getConstructor(String.class).newInstance(msg);
        } catch (InstantiationException    | IllegalAccessException | IllegalArgumentException | 
                 InvocationTargetException | NoSuchMethodException  | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    public <T extends Exception> void signalException(Enum<?>   anomaly, 
                                                      Throwable cause,
                                                      Class<T>  excepClass, 
                                                      Object... args) throws T {

    	String msg = signalImpl(anomaly, 1, args);
        try {
            throw excepClass.getConstructor(String.class, Throwable.class).newInstance(msg, cause);
        } catch (InstantiationException    | IllegalAccessException | IllegalArgumentException | 
                 InvocationTargetException | NoSuchMethodException  | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String signalImpl(Enum<?> anomaly, int stackTraceMod, Object...args) {
    	checkArgument(currTask.isPresent(), "Cannot signal anomalies when no task is started");
        Anomaly ano = Anomaly.of(anomaly, currTask.get().getGeneration(), stackTraceMod + 1);
        currTask.get().addObservation(ano);
        return ano.getMessage();
    }
    
    
    //
    //  ~  SETTERS  ~  //
    //

    public void setVerboseLevel(Level verbLvl) {
        this.verbLvl = verbLvl;
    }
    
    
    //
	//  ~  RESET  ~  //
	//
    
    public void reset() {
    	completedTask.clear();
    	currTask = Optional.empty();
    }
    
    
    //
    //  ~  GETTERS  ~  //
    //

    public ImmutableList<Task> getCompletedTasks() {
        return ImmutableList.copyOf(completedTask);
    }
}
