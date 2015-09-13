package com.atompacman.nrstep;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.atompacman.configuana.Lib;
import com.atompacman.configuana.StrictParam;
import com.atompacman.toolkat.misc.Log;

public class NRStep extends Lib {

    //======================================= METHODS ============================================\\

    public List<Class<? extends StrictParam>> getParamsClasses() {
        return new ArrayList<>();
    }

    public void init() {
        Log.title(Level.INFO, "%s %s", getName(), getVersion());
    }

    public void finalize() {

    }
}
