package com.atompacman.klusterz.algorithm;

import java.util.Arrays;
import java.util.List;

import com.atompacman.atomlog.Log;
import com.atompacman.klusterz.container.Element;
import com.atompacman.klusterz.container.KClass;
import com.atompacman.klusterz.initialMeans.InitialMeansSelection;

public abstract class ClusteringAlgorithm {

	private static final int MAX_NB_CLASSES_TO_LOG_MEANS = 20;
	private static final String MEAN_COMPONENT_FORMATING = "%6.2f";

	
	protected int nbDimensions;
	protected int nbClasses;

	protected Element[] elements;
	protected KClass[] classes;
	
	private final InitialMeansSelection initialMeansSelector;


	//------------ PROTECTED CONSTRUCTOR ------------\\

	protected ClusteringAlgorithm(int nbClasses, 
								  Element[] elements, 
								  InitialMeansSelection initialMeansSelector) {
		
		this.nbDimensions = elements[0].components.length;
		this.nbClasses = nbClasses;
	
		this.elements = elements;
		
		this.initialMeansSelector = initialMeansSelector;
	}
	
	
	//------------ EXECUTE ------------\\

	public List<KClass> execute() {
		if (Log.infos() && Log.print("Initial means selection"));
		classes = initialMeansSelector.selectInitialMeans(elements);
		
		int nbIterations = 0;
		
		do {
			logMeans(nbIterations);
			classElements();
			++nbIterations;
		} while(!updateClassesUntilConvergence());

		logMeans(nbIterations);
		
		if (Log.infos() && Log.print("Total iterations performed: " + nbIterations));
		
		return Arrays.asList(classes);
	}
	
	private void classElements() {
		for (KClass kClass : classes) {
			kClass.elementsIndex.clear();
		}
		for (int i = 0; i < elements.length; ++i) {
			Element element = elements[i];
			double minDistance = Double.MAX_VALUE;
			KClass closestClass = null;
			
			for (KClass kClass : classes) {
				double distance = element.squareEuclidDistFrom(kClass.mean);
				if (distance < minDistance) {
					minDistance = distance;
					closestClass = kClass;
				}
			}
			closestClass.elementsIndex.add(i);
		}
	}
	
	public abstract boolean updateClassesUntilConvergence();
	

	//------------ LOG MEANS ------------\\

	private void logMeans(int nbIterations) {
		if (nbClasses > MAX_NB_CLASSES_TO_LOG_MEANS) {
			return;
		}
		
		StringBuilder builder = new StringBuilder();

		builder.append("Iter#");
		builder.append(String.format("%2d", nbIterations + 1));
		builder.append(" ~ ");
		
		for (KClass kClass : classes) {
			builder.append("[");
			
			double[] components = kClass.mean.components;
			builder.append(String.format(MEAN_COMPONENT_FORMATING, components[0]));

			for (int d = 1; d < nbDimensions; ++d) {
				builder.append("|");
				builder.append(String.format(MEAN_COMPONENT_FORMATING, components[d]));
			}
			builder.append("] ");
		}
		if (Log.extra() && Log.print(builder.toString()));
	}
}
