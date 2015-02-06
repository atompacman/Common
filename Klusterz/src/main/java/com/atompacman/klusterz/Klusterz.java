package com.atompacman.klusterz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.atompacman.atomlog.Log;
import com.atompacman.configuana.App;
import com.atompacman.configuana.Cmd;
import com.atompacman.configuana.param.Param;
import com.atompacman.klusterz.Parameters.Misc;
import com.atompacman.klusterz.Parameters.Paths;
import com.atompacman.klusterz.algorithm.ClusteringAlgorithm;
import com.atompacman.klusterz.algorithm.KMeans;
import com.atompacman.klusterz.app.CartesianPlanClustering;
import com.atompacman.klusterz.app.ImageColorSegmentation;
import com.atompacman.klusterz.container.ClusteringPlan;
import com.atompacman.klusterz.container.Element;
import com.atompacman.klusterz.container.KClass;
import com.atompacman.klusterz.initialMeans.InitialMeansSelection;
import com.atompacman.klusterz.initialMeans.MajorityMeansSelection;
import com.atompacman.klusterz.initialMeans.RandomMeansSelection;
import com.atompacman.toolkat.test.TestFileDetector;

public final class Klusterz extends App {
	
	//======================================= FIELDS =============================================\\

	public Random randGen;
	
	
	
	//======================================= METHODS ============================================\\

	//---------------------------------------- INIT ----------------------------------------------\\

	public void init() {
		if (Log.infos() && Log.title("KLUSTERZ"));
		
		TestFileDetector.setPackagePathToRemove(Klusterz.class.getPackage().getName());
		TestFileDetector.setTestDirectory(Paths.TEST_DIRECTORY);
		randGen = new Random(getDefaultProfile().getLong(Misc.RANDOM_SEED));
	}
	
	
	//--------------------------------------- EXECUTE --------------------------------------------\\

	public static List<KClass> execute(ClusteringPlan plan) {
		if (Log.infos() && Log.print("Beginning clustering"));

		if (plan.getAlgorithm()    == null || 
			plan.getElements()     == null || 
			plan.getInitialMeans() == null) {
			throw new NullPointerException();
		}

		int nbClasses = plan.getNbClasses();
		Element[] elements = plan.getElements();
		int nbElements = elements.length;

		if (nbClasses < 1) {
			throw new IllegalArgumentException("The number of class must be a positive integer.");
		}

		if (nbElements == 0) {
			throw new IllegalArgumentException("The number of elements must not be zero.");
		}

		if (nbElements < nbClasses) {
			if (Log.warng() && Log.print("More classes than elements: Using trivial solution"));
			return trivialSolution(elements);
		}

		if (!plan.getAlgorithm().isCompatibleWith(plan.getInitialMeans())) {
			throw new IllegalArgumentException("The " + plan.getAlgorithm() + 
					" algorithm is not compatible with the " + plan.getInitialMeans() + 
					" initial means selection method.");
		}

		InitialMeansSelection meansSelector = null;

		switch (plan.getInitialMeans()) {
		case RANDOM:
			if (plan.getDimensionsMinima() == null) {
				throw new IllegalArgumentException("Minima for element dimensions "
						+ "must be set for a random initial mean selection.");
			}
			if (plan.getDimensionsMaxima() == null) {
				throw new IllegalArgumentException("Maxima for element dimensions "
						+ "must be set for a random initial mean selection.");
			}
			if (plan.getDimensionsMinima().length != elements[0].components.length ||
					plan.getDimensionsMaxima().length != elements[0].components.length) {
				throw new IllegalArgumentException("Extremum dimensions "
						+ "does not match elements dimensions.");
			}
			meansSelector = new RandomMeansSelection(nbClasses, 
					plan.getDimensionsMinima(), plan.getDimensionsMaxima());
			break;

		case MAJORITY:
			meansSelector = new MajorityMeansSelection(nbClasses);
			break;

		default:
			throw new RuntimeException("Unimplemented initial mean slection method.");
		}

		ClusteringAlgorithm algorithm = null;

		switch (plan.getAlgorithm()) {
		case K_MEANS:
			algorithm = new KMeans(nbClasses, elements, meansSelector);
			break;
		default:
			throw new RuntimeException("Unimplemented algorithm.");
		}

		return algorithm.execute();
	}

	private static List<KClass> trivialSolution(Element[] elements) {
		List<KClass> clusters = new ArrayList<KClass>();

		for (int i = 0; i < elements.length; ++i) {
			clusters.add(new KClass(elements[i]));
		}

		return clusters;
	}

	
	//--------------------------------------- GETTERS --------------------------------------------\\

	public List<Class<? extends Cmd<?, ?>>> getCmdClasses() {
		List<Class<? extends Cmd<?, ?>>> cmdClasses = new ArrayList<>();
		cmdClasses.add(CartesianPlanClustering.class);
		cmdClasses.add(ImageColorSegmentation.class);
		return cmdClasses;
	}

	public List<Class<? extends Param>> getParamsClasses() {
		List<Class<? extends Param>> paramClasses = new ArrayList<>();
		paramClasses.add(Parameters.class);
		return paramClasses;
	}


	//--------------------------------------- SHUTDOWN -------------------------------------------\\

	public void shutdown() {
		
	}
}
