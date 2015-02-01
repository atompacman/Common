package com.atompacman.klusterz.initialMeans;

import com.atompacman.klusterz.container.Element;
import com.atompacman.klusterz.container.KClass;

public abstract class InitialMeansSelection {

	protected final int nbClasses;

	
	//------------ PROTECTED CONSTRUCTOR ------------\\

	protected InitialMeansSelection(int nbClasses) {
		this.nbClasses = nbClasses;
	}
	
	
	//------------ SELECT INITIAL MEANS ------------\\

	public abstract KClass[] selectInitialMeans(Element[] elements);
}
