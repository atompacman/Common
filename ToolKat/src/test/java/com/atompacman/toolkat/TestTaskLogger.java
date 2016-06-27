package com.atompacman.toolkat;

import org.apache.logging.log4j.Level;

import com.atompacman.toolkat.task.AnomalyDescription;
import com.atompacman.toolkat.task.AnomalyDescription.Severity;
import com.atompacman.toolkat.task.ReportViewer;
import com.atompacman.toolkat.task.TaskDescription;
import com.atompacman.toolkat.task.TaskLogger;

public final class TestTaskLogger {

    private static final class A {
        
        private enum Task {
            @TaskDescription(nameFormat = "A1a")
            A1a,
            @TaskDescription(nameFormat = "A1b - %s")
            A1b,
            @TaskDescription(nameFormat = "A2")
            A2
        }
        
        private enum Anomaly {
            @AnomalyDescription (consequences  = "YOU DIE", 
                                 detailsFormat = "Black plague",
                                 description   = "Cahoy",
                                 name          = "Uh oh", 
                                 severity      = Severity.CRITIC)
            UH_OH
        }
        
        private TaskLogger taskLogger;
        
        public A(TaskLogger taskLogger) {
            this.taskLogger = taskLogger;
        }
        
        public void one() {
            taskLogger.startTask(Task.A1a);
            taskLogger.log("Ayoyoyo");
            taskLogger.log(0, "Ayoyoyo");
            taskLogger.log(Level.ERROR, 0, "Ayoyoyo");
            taskLogger.log(Level.ERROR, "Ayoyoyo");
            
            taskLogger.startTask(Task.A1b, "goglu");
            taskLogger.signal(Anomaly.UH_OH, "world");
        }
        
        public void two() {
            taskLogger.startTask(Task.A2);
            taskLogger.log("Vilbrequin");
            
            B b = new B(taskLogger);
            b.uno();
        }
    }
    
    private static final class B {
        
        private enum Task {
            @TaskDescription("Yolo")
            YOLO,
            @TaskDescription("Allo")
            ALLO
        }
        
        private TaskLogger taskLogger;
        
        public B(TaskLogger taskLogger) {
            this.taskLogger = taskLogger;
        }
        
        public void uno() {
            taskLogger.startSubtask(Task.YOLO);
            taskLogger.log("Cock");
            taskLogger.startTask(Task.ALLO);
        }
        
        public void dos() {
            
        }
    }
    
    private static final class C {
        
        private TaskLogger taskLogger;
        
        public C(TaskLogger taskLogger) {
            this.taskLogger = taskLogger;
        }
        
        public void ein() {
            
        }
        
        public void zwei() {
            
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        TaskLogger taskLogger = TaskLogger.of();
        A a = new A(taskLogger);
        a.one();
        a.two();
        ReportViewer.showReportWindow(taskLogger);
    }
}
