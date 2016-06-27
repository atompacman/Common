package com.atompacman.toolkat.task;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableList;

public final class TaskMonitor {

    //
    //  ~  INNER TYPES  ~  //
    //
    
    @FunctionalInterface
    public interface Task<T, E extends Exception> {
        T execute(TaskMonitor monitor) throws E;
    }
    
    @FunctionalInterface
    public interface SafeTask<T> {
        T executeSafe(TaskMonitor monitor);
    }
    
    
    //
    //  ~  FIELDS  ~  //
    //

    private final String taskName;
    private final String taskDesc;
    private final Level  verbLvl;
    
    private final List<Observation>       obs;
    private final Optional<TaskMonitor>   parentTask;
    private final LinkedList<TaskMonitor> subtasks;
    private final StopWatch               time;
    
    
    //
    //  ~  INIT  ~  //
    //

    public static TaskMonitor of(String taskName) {
        return new TaskMonitor(taskName, "UNSPECIFIED", Level.INFO, Optional.empty());
    }
    
    public static TaskMonitor of(String taskName, String taskDescription) {
        return new TaskMonitor(taskName, taskDescription, Level.INFO, Optional.empty());
    }
    
    public static TaskMonitor of(String taskNameFormat, Level verbLvl) {
        return new TaskMonitor(taskNameFormat, "UNSPECIFIED", verbLvl, Optional.empty());
    }
    
    public static TaskMonitor of(String taskNameFormat, String taskDescription, Level verbLvl) {
        return new TaskMonitor(taskNameFormat, taskDescription, verbLvl, Optional.empty());
    }
    
    private TaskMonitor(String                taskName, 
                        String                taskDesc, 
                        Level                 verbLvl,
                        Optional<TaskMonitor> parentTask) {
        
        this.taskName = taskName;
        this.taskDesc = taskDesc;
        this.verbLvl  = verbLvl;
        
        this.obs        = new LinkedList<>();
        this.parentTask = parentTask;
        this.subtasks   = new LinkedList<>();
        this.time       = new StopWatch();
    }
    
    
    //
    //  ~  EXECUTE SUBTASKS  ~  //
    //
    
    public <T, E extends Exception> 
    T executeSubtask(String taskName, Task<T,E> task) throws E {
        return executeSubtaskImpl(taskName, "UNSPECIFIED", verbLvl, task, null, false);
    }
    
    public <T, E extends Exception> 
    T executeSubtask(String taskName, String taskDesc, Task<T,E> task) throws E {
        return executeSubtaskImpl(taskName, taskDesc, verbLvl, task, null, false);
    }
    
    public <T, E extends Exception> 
    T executeSubtask(String taskName, Level verbLvl, Task<T,E> task) throws E {
        return executeSubtaskImpl(taskName, "UNSPECIFIED", verbLvl, task, null, false);
    }
    
    public <T, E extends Exception> 
    T executeSubtask(String taskName, String taskDesc, Level verbLvl, Task<T,E> task) throws E {
        return executeSubtaskImpl(taskName, taskDesc, verbLvl, task, null, false);
    }
    
    public <T> 
    T executeSafeSubtask(String taskName, SafeTask<T> task) {
        return executeSubtaskImpl(taskName, "UNSPECIFIED", verbLvl, null, task, true);
    }
    
    public <T> 
    T executeSafeSubtask(String taskName, String taskDesc, SafeTask<T> task) {
        return executeSubtaskImpl(taskName, taskDesc, verbLvl, null, task, true);
    }
    
    public <T> 
    T executeSafeSubtask(String taskName, Level verbLvl, SafeTask<T> task) {
        return executeSubtaskImpl(taskName, "UNSPECIFIED", verbLvl, null, task, true);
    }
    
    public <T> 
    T executeSafeSubtask(String taskName, String taskDesc, Level verbLvl, SafeTask<T> task) {
        return executeSubtaskImpl(taskName, taskDesc, verbLvl, null, task, true);
    }
    
    private <T, E extends Exception> T executeSubtaskImpl(String      taskName,
                                                          String      taskDesc,
                                                          Level       verbLvl,
                                                          Task<T,E>   task,
                                                          SafeTask<T> safeTask,
                                                          boolean     isSafe) throws E {
        // Create subtask monitor
        TaskMonitor submonitor = new TaskMonitor(taskName, taskDesc, verbLvl, Optional.of(this));
        
        // Start timer
        submonitor.time.start();
        
        // Execute task
        T t = isSafe ? safeTask.executeSafe(submonitor) : task.execute(submonitor);
        
        // Stop timer
        submonitor.time.stop();
        
        // Save monitored subtasks
        subtasks.add(submonitor);
        
        return t;
    }
 
    
    //
    //  ~  LOG  ~  //
    //
    
    public void log(String format, Object...args) {
        logImpl(Optional.empty(), 1, format, args);
    }

    public void log(int stackTraceMod, String format, Object...args) {
        logImpl(Optional.empty(), stackTraceMod + 1, format, args);
    }

    public void log(Level verbose, String format, Object...args) {
        logImpl(Optional.of(verbose), 1, format, args);
    }

    public void log(Level verbose, int stackTraceMod, String format, Object...args) {
        logImpl(Optional.of(verbose), stackTraceMod + 1, format, args);
    }
    
    private void logImpl(Optional<Level> verbose, int stackTraceMod, String format, Object...args) {
        // Create full message
        String msg = new StringBuilder().append(getHierarchicalName())
                                        .append(' ')
                                        .append(String.format(format, args)).toString();
        // Get message verbose level
        Level lvl = verbose.isPresent() ? verbose.get() : verbLvl;
        
        // Create log observation
        obs.add(new Observation(msg, lvl, stackTraceMod + 1));
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

        signalExceptionImpl(anomaly, excepClass, Optional.empty(), args);
    }
    
    public <T extends Exception> void signalException(Enum<?>   anomaly,
                                                      Class<T>  excepClass,
                                                      Throwable cause,
                                                      Object... args) throws T {

        signalExceptionImpl(anomaly, excepClass, Optional.of(cause), args);
    }
    
    public <T extends Exception> void signalExceptionImpl(Enum<?>             anomaly,
                                                          Class<T>            excepClass, 
                                                          Optional<Throwable> cause,
                                                          Object...           args) throws T {

        // Log anomaly
        String msg = signalImpl(anomaly, 2, args);
        
        // Throw exception with or without a cause
        try {
            if (cause.isPresent()) {
                throw excepClass.getConstructor(String.class, Throwable.class)
                                .newInstance(msg, cause.get());
            } else {
                throw excepClass.getConstructor(String.class)
                                .newInstance(msg);
            }
        } catch (InstantiationException    | IllegalAccessException | IllegalArgumentException | 
                 InvocationTargetException | NoSuchMethodException  | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String signalImpl(Enum<?> anomaly, int stackTraceMod, Object...args) {
        // Create anomaly
        Anomaly ano = Anomaly.of(anomaly, stackTraceMod + 1, args);
        obs.add(ano);
        return ano.getMessage();
    }
    

    //
    //  ~  GETTERS  ~  //
    //

    public String getTaskName() {
        return taskName;
    }

    public String getHierarchicalName() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        TaskMonitor task = getRootTask();
        sb.append(task.taskName);
        LinkedList<TaskMonitor> subs = task.subtasks;
        while (!subs.isEmpty()) {
            sb.append(subs.getLast().taskName).append(" | ");
            subs = subs.getLast().subtasks;
        }
        return sb.append(']').toString();
    }
    
    private TaskMonitor getRootTask() {
        return parentTask.isPresent() ? parentTask.get().getRootTask() : this;
    }
    
    public String getDescription() {
        return taskDesc;
    }

    
    public ImmutableList<Observation> getObservations() {
        return ImmutableList.copyOf(obs);
    }
    
    public ImmutableList<TaskMonitor> getSubtasks() {
        return ImmutableList.copyOf(subtasks);
    }
    
    
    //
    //  ~  SERIALIZATION  ~  //
    //

    @Override
    public String toString() {
        return getHierarchicalName();
    }
}
