package com.atompacman.klusterz.algorithm;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.atompacman.klusterz.container.Element;
import com.atompacman.klusterz.container.KClass;
import com.atompacman.klusterz.initialMeans.InitialMeansSelection;

public abstract class ClusteringAlgorithm {
	
	//====================================== CONSTANTS ===========================================\\

	private static final Logger logger = LogManager.getLogger(ClusteringAlgorithm.class);
	
	private static final int MAX_NB_CLASSES_TO_LOG_MEANS = 20;
	private static final String MEAN_COMPONENT_FORMATING = "%6.2f";

	
	
	//======================================= FIELDS =============================================\\

	protected int numDim;
	protected int numClasses;

	protected final Element[] elements;
	protected KClass[] 		  classes;
	
	private final InitialMeansSelection initialMeansSelector;


	
	//=================================== ABSTRACT METHODS =======================================\\

	//--------------------------------------- EXECUTE --------------------------------------------\\

	public abstract boolean updateClassesUntilConvergence();

	
	
	//======================================= METHODS ============================================\\

	//--------------------------------- PROTECTED CONSTRUCTOR -------------------------------------\\

	protected ClusteringAlgorithm(int nbClasses, 
								  Element[] elements, 
								  InitialMeansSelection initialMeansSelector) {
		
		this.numDim = elements[0].components.length;
		this.numClasses = nbClasses;
	
		this.elements = elements;
		
		this.initialMeansSelector = initialMeansSelector;
	}
		
	
	//--------------------------------------- EXECUTE --------------------------------------------\\

	public List<KClass> execute() {
		logger.info("Initial means selection");
		classes = initialMeansSelector.selectInitialMeans(elements);
		
		int numIterations = 0;
		
		do {
			logMeans(numIterations);
			classElements();
			++numIterations;
		} while(!updateClassesUntilConvergence());

		logMeans(numIterations);
		
		logger.info("Total iterations performed: {}", numIterations);
		
		return Arrays.asList(classes);
	}
	
	private void logMeans(int nbIterations) {
		if (numClasses > MAX_NB_CLASSES_TO_LOG_MEANS) {
			return;
		}
		
		StringBuilder builder = new StringBuilder();

		builder.append("Iter#");
		builder.append(String.format("%2d", nbIterations + 1));
		builder.append(" ~ ");
		
		for (KClass kClass : classes) {
			builder.append('[');
			
			double[] components = kClass.mean.components;
			builder.append(String.format(MEAN_COMPONENT_FORMATING, components[0]));

			for (int d = 1; d < numDim; ++d) {
				builder.append('|');
				builder.append(String.format(MEAN_COMPONENT_FORMATING, components[d]));
			}
			builder.append("] ");
		}
		logger.debug(builder.toString());
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
}
