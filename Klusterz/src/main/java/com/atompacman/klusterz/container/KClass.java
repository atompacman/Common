package com.atompacman.klusterz.container;

import java.util.HashSet;
import java.util.Set;

public final class KClass {

	public Element mean;
	public Set<Integer> elementsIndex;
	
	
	//------------ PUBLIC CONSTRUCTOR ------------\\

	public KClass(Element mean) {
		this.mean = mean;
		this.elementsIndex = new HashSet<Integer>();
	}


	//------------ EQUALS ------------\\
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KClass other = (KClass) obj;
		if (elementsIndex == null) {
			if (other.elementsIndex != null)
				return false;
		} else if (!elementsIndex.equals(other.elementsIndex))
			return false;
		if (mean == null) {
			if (other.mean != null)
				return false;
		} else if (!mean.equals(other.mean))
			return false;
		return true;
	}
}
