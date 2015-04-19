package com.atompacman.configuana;

import java.util.List;

public abstract class App extends Lib {

    //=================================== ABSTRACT METHODS =======================================\\

    public abstract List<Class<? extends Cmd<?, ?>>> getCmdClasses();
}
