package com.atompacman.klusterz.container;

import java.util.Arrays;

public class Element {

	public double[] components;
	
	
	//------------ PUBLIC CONSTRUCTORS ------------\\

	public Element(double[] components) {
		this.components = components;
	}
	
	public Element(final Element toCopy) {
		components = Arrays.copyOf(toCopy.components, toCopy.components.length);
	}
	
	
	//------------ EUCLIDIAN DISTANCE OF ------------\\

	public double squareEuclidDistFrom(Element other) {
		if (components.length != other.components.length) {
			throw new IllegalArgumentException("Compared elements must be of the same dimensions.");
		}

		double distance = 0;
		
		for (int d = 0; d < components.length; ++d) {
			double delta = components[d] - other.components[d];
			distance += delta * delta;
		}
		
		return distance;
	}

	
	//------------ EQUALS ------------\\
	
	public int hashCode() {
		return Arrays.hashCode(components);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Element other = (Element) obj;
		if (!Arrays.equals(components, other.components))
			return false;
		return true;
	}
}
