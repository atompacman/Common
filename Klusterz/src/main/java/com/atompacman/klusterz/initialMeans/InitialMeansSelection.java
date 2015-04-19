package com.atompacman.klusterz.initialMeans;

import com.atompacman.klusterz.container.Element;
import com.atompacman.klusterz.container.KClass;

public abstract class InitialMeansSelection {

    //======================================= FIELDS =============================================\\

    protected final int numClasses;



    //======================================= METHODS ============================================\\

    //--------------------------------- PROTECTED CONSTRUCTOR ------------------------------------\\

    protected InitialMeansSelection(int nbClasses) {
        this.numClasses = nbClasses;
    }


    //--------------------------------- SELECT INITIAL MEANS -------------------------------------\\

    public abstract KClass[] selectInitialMeans(Element[] elements);
}
