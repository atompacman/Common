package com.atompacman.configuana;

import java.util.ArrayList;
import java.util.List;

import com.atompacman.configuana.DummyConsoleApps.HasFlagWithNegNumArgs.NegNumArgs;

public class DummyConsoleApps {

    public static class NotImplementingApp {}

    public static class DoesntHaveAnEmptyConstructor extends App {
        public DoesntHaveAnEmptyConstructor(String s) {}
        public List<Class<? extends Cmd<?,?>>> getCmdClasses() { return null; }
        public String appName() { return null; }
        public List<Class<? extends StrictParam>> getParamsClasses() { return null; }
        public void init() {}
        public void finalize() {}
    }

    public static class HasACmdClassWoutAnEmptyConstr extends App {

        public static class WithoutAnEmptyConstructor 
        implements Cmd<HasACmdClassWoutAnEmptyConstr, Flagz> {
            public WithoutAnEmptyConstructor(String s) {}
            public void execute(HasACmdClassWoutAnEmptyConstr app, CmdArgs<Flagz> appCmdArgs) {}
            public CmdInfo info() { return null; }
        }

        public enum Flagz implements Flag {
            YOLO {
                public FlagInfo info() { return new FlagInfo("nna", 
                        "Minimum number of calories", 2,
                        "Specifies a minimum number of calories"); }
            }
        }

        public List<Class<? extends Cmd<?,?>>> getCmdClasses() { 
            List<Class<? extends Cmd<?,?>>> cmdClasses = new ArrayList<>();
            cmdClasses.add(WithoutAnEmptyConstructor.class);
            return cmdClasses; 
        }
        public List<Class<? extends StrictParam>> getParamsClasses() { return null; }
        public void init() {}
        public void finalize() {}
    }

    public static class HasACmdClassThatExtFlag extends App {

        public static class ExtendsFlag implements Cmd<HasACmdClassThatExtFlag,Flag> {
            public void execute(HasACmdClassThatExtFlag app, CmdArgs<Flag> appCmdArgs) {}
            public CmdInfo info() { return null; }
        }

        public List<Class<? extends Cmd<?,?>>> getCmdClasses() { 
            List<Class<? extends Cmd<?,?>>> cmdClasses = new ArrayList<>();
            cmdClasses.add(ExtendsFlag.class);
            return cmdClasses; 
        }

        public List<Class<? extends StrictParam>> getParamsClasses() { return null; }
        public void init() {}
        public void finalize() {}
    }

    public static class HasNonEnumFlagClass extends App {

        public static class NonEnumFlagCmd implements Cmd<HasNonEnumFlagClass,NonEnumFlag> {
            public void execute(HasNonEnumFlagClass app, CmdArgs<NonEnumFlag> appCmdArgs) {}
            public CmdInfo info() { return null; }
        }

        public static class NonEnumFlag implements Flag {
            public FlagInfo info() { return null;}
        }

        public List<Class<? extends Cmd<?,?>>> getCmdClasses() {
            List<Class<? extends Cmd<?,?>>> cmdClasses = new ArrayList<>();
            cmdClasses.add(NonEnumFlagCmd.class);
            return cmdClasses; 
        }

        public List<Class<? extends StrictParam>> getParamsClasses() { return null; }
        public void init() {}
        public void finalize() {}
    }

    public static class HasFlagWithNegNumArgs extends App {

        public static class NNA implements Cmd<HasFlagWithNegNumArgs,NegNumArgs> {
            public void execute(HasFlagWithNegNumArgs app, CmdArgs<NegNumArgs> appCmdArgs) {}
            public CmdInfo info() { return null; }
        }

        public enum NegNumArgs implements Flag {
            YOLO {
                public FlagInfo info() { return new FlagInfo("nna", 
                        "Minimum number of calories", -3,
                        "Specifies a minimum number of calories"); }
            }
        }

        public List<Class<? extends Cmd<?,?>>> getCmdClasses() {
            List<Class<? extends Cmd<?,?>>> cmdClasses = new ArrayList<>();
            cmdClasses.add(NNA.class);
            return cmdClasses; 
        }

        public List<Class<? extends StrictParam>> getParamsClasses() { return null; }
        public void init() {}
        public void finalize() {}
    }

    public static class HasCmdWithNegMainArgs extends App {

        public static class NegMainArgs implements Cmd<HasCmdWithNegMainArgs,NegNumArgs> {
            public void execute(HasCmdWithNegMainArgs app, CmdArgs<NegNumArgs> appCmdArgs) {}
            public CmdInfo info() { return new CmdInfo("", "", -3, ""); }
        }

        public List<Class<? extends Cmd<?,?>>> getCmdClasses() {
            List<Class<? extends Cmd<?,?>>> cmdClasses = new ArrayList<>();
            cmdClasses.add(NegMainArgs.class);
            return cmdClasses; 
        }

        public List<Class<? extends StrictParam>> getParamsClasses() { return null; }
        public void init() {}
        public void finalize() {}
    }
}	