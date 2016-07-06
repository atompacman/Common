package com.atompacman.toolkat.task;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.Log;
import com.google.common.collect.ImmutableList;

public final class TaskMonitor {

    //
    //  ~  INNER TYPES  ~  //
    //
    
    @FunctionalInterface
    public interface Task<T> {
        T execute(TaskMonitor monitor);
    }
    
    @FunctionalInterface
    public interface TaskExcep<T, E extends Exception> {
        T execute(TaskMonitor monitor) throws E;
    }

    @FunctionalInterface
    public interface SideEffectTask {
        void execute(TaskMonitor monitor);
    }
    
    @FunctionalInterface
    public interface SideEffectTaskExcep<E extends Exception> {
        void execute(TaskMonitor monitor) throws E;
    }
    
    @FunctionalInterface
    public interface SideEffetMonoArgTask<T> {
        void execute(TaskMonitor monitor, T arg);
    }
    
    @FunctionalInterface
    public interface SideEffetMonoArgTaskExcep<T, E extends Exception> {
        void execute(TaskMonitor monitor, T arg) throws E;
    }
    
    
    //
    //  ~  FIELDS  ~  //
    //

    private final String taskName;
    private final String taskDesc;
    
    private Level  verbLvl;
    
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

    public <T> T executeSubtask(String taskName, Task<T> task) {
        return executeSubtaskImpl(taskName, "UNSPECIFIED", verbLvl, task);
    }
    
    public <T> T executeSubtask(Enum<?> taskDesc, Task<T> task) {
        // TODO implement
        return null;
    }
    
    private <T> T executeSubtaskImpl(String  taskName,
                                     String  taskDesc,
                                     Level   verbLvl,
                                     Task<T> task) {
        
        TaskMonitor submonitor = startSubmonitor(taskName, taskDesc, verbLvl);
        T t = task.execute(submonitor);
        stopSubmonitor(submonitor);
        return t;
    }
    
    public <T, E extends Exception> T executeSubtaskExcep(String         taskName, 
                                                          TaskExcep<T,E> task) throws E {
        
        return executeSubtaskImpl(taskName, "UNSPECIFIED", verbLvl, task);
    }
    
    private <T, E extends Exception> T executeSubtaskImpl(String         taskName,
                                                          String         taskDesc,
                                                          Level          verbLvl,
                                                          TaskExcep<T,E> task) throws E {

        TaskMonitor submonitor = startSubmonitor(taskName, taskDesc, verbLvl);
        T t = task.execute(submonitor);
        stopSubmonitor(submonitor);
        return t;
    }

    public void executeSubtask(String taskName, SideEffectTask task) {
        executeSubtaskImpl(taskName, "UNSPECIFIED", verbLvl, task);
    }
    
    private void executeSubtaskImpl(String         taskName,
                                    String         taskDesc,
                                    Level          verbLvl,
                                    SideEffectTask task) {

        TaskMonitor submonitor = startSubmonitor(taskName, taskDesc, verbLvl);
        task.execute(submonitor);
        stopSubmonitor(submonitor);
    }
    
    public <E extends Exception> void executeSubtaskExcep(String                 taskName, 
                                                          SideEffectTaskExcep<E> task) throws E {
        executeSubtaskImpl(taskName, "UNSPECIFIED", verbLvl, task);
    }
    
    private <E extends Exception> void executeSubtaskImpl(String                 taskName,
                                                          String                 taskDesc,
                                                          Level                  verbLvl,
                                                          SideEffectTaskExcep<E> task) throws E{

        TaskMonitor submonitor = startSubmonitor(taskName, taskDesc, verbLvl);
        task.execute(submonitor);
        stopSubmonitor(submonitor);
    }
    
    public <T> void executeSubtask(String taskName, T arg, SideEffetMonoArgTask<T> task) {
        executeSubtaskImpl(taskName, "UNSPECIFIED", verbLvl, task, arg);
    }
    
    private <T> void executeSubtaskImpl(String                  taskName,
                                        String                  taskDesc,
                                        Level                   verbLvl,
                                        SideEffetMonoArgTask<T> task,
                                        T                       arg) {

        TaskMonitor submonitor = startSubmonitor(taskName, taskDesc, verbLvl);
        task.execute(submonitor, arg);
        stopSubmonitor(submonitor);
    }
    
    public <T, E extends Exception> void executeSubtaskExcep(String                        taskName, 
                                                             T                             arg, 
                                                             SideEffetMonoArgTaskExcep<T,E>task) 
                                                                                          throws E {
        executeSubtaskImpl(taskName, "UNSPECIFIED", verbLvl, task, arg);
    }
    
    private <T, E extends Exception> void executeSubtaskImpl(String                        taskName,
                                                             String                        taskDesc,
                                                             Level                         verbLvl,
                                                             SideEffetMonoArgTaskExcep<T,E>task,
                                                             T                             arg) 
                                                                                           throws E{

        TaskMonitor submonitor = startSubmonitor(taskName, taskDesc, verbLvl);
        task.execute(submonitor, arg);
        stopSubmonitor(submonitor);
    }
    
    private TaskMonitor startSubmonitor(String taskName, String taskDesc, Level verbLvl) {
        // Create subtask monitor
        TaskMonitor submonitor = new TaskMonitor(taskName, taskDesc, verbLvl, Optional.of(this));
        
        // Save monitored subtasks
        subtasks.add(submonitor);
        
        // Start timer
        submonitor.time.start();
        Log.log(verbLvl, 3, "%s Start", submonitor.getHierarchicalName());
        
        return submonitor;
    }
    
    private void stopSubmonitor(TaskMonitor submonitor) {
     // Stop timer
        submonitor.time.stop();
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
        String name = getHierarchicalName();
        String msg  = String.format(format, args);
        Level lvl   = verbose.isPresent() ? verbose.get() : verbLvl;
        obs.add(new Observation(name, msg, lvl, stackTraceMod + 1));
    }
    
    
    //
    //  ~  ANOMALIES  ~  //
    //
    
    public void signal(Enum<?> anomaly, Object...args) {
        signalImpl(anomaly, 1, args);
    }

    public <T extends Exception, U> U signalException(Enum<?>   anomaly, 
                                                      Class<T>  excepClass, 
                                                      Object... args) throws T {

        signalExceptionImpl(anomaly, excepClass, Optional.empty(), args);
        return null;
    }
    
    public <T extends Exception, U> U signalException(Enum<?>   anomaly,
                                                      Class<T>  excepClass,
                                                      Throwable cause,
                                                      Object... args) throws T {

        signalExceptionImpl(anomaly, excepClass, Optional.of(cause), args);
        return null;
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
        Anomaly ano = Anomaly.of(anomaly, getHierarchicalName(), stackTraceMod + 1, args);
        obs.add(ano);
        return ano.getMessage();
    }
    
    
    //
    //  ~  SETTERS  ~  //
    //
    
    public void setDefaultVerbose(Level verbLvl) {
        this.verbLvl = verbLvl;
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
            sb.append('|').append(subs.getLast().taskName);
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
