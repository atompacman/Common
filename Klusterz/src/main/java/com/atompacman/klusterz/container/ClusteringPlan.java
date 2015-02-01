package com.atompacman.klusterz.container;

public final class ClusteringPlan {

	private static final InitialMeans DEFAULT_INITIAL_MEANS = InitialMeans.RANDOM;


	public enum Algorithm { 
		K_MEANS (InitialMeans.RANDOM, InitialMeans.MAJORITY), 
		K_MEDOIDS (InitialMeans.MAJORITY); 

		private InitialMeans[] validInitialMeansSelectors;


		private Algorithm(InitialMeans... validInitialMeansSelectors) {
			this.validInitialMeansSelectors = validInitialMeansSelectors;
		}

		public boolean isCompatibleWith(InitialMeans initialMeansSelector) {
			for (InitialMeans validInitialMeans : validInitialMeansSelectors) {
				if (validInitialMeans == initialMeansSelector) {
					return true;
				}
			}
			return false;
		}
	}

	public enum InitialMeans { RANDOM, MAJORITY; }


	private Algorithm algorithm;
	private Element[] elements;
	private int nbClasses;

	private InitialMeans initialMeans;
	private double[] dimensionsMinima; 
	private double[] dimensionsMaxima;


	//------------ PUBLIC CONSTRUCTORS ------------\\

	public ClusteringPlan(Algorithm algorithm, 
						  Element[] elements, 
						  int nbClasses) {

		this(algorithm, elements, nbClasses, DEFAULT_INITIAL_MEANS);
	}

	public ClusteringPlan(Algorithm algorithm, 
						  Element[] elements, 
						  int nbClasses, 
						  InitialMeans initialMeans) {

		this.algorithm = algorithm;
		this.elements = elements;
		this.nbClasses = nbClasses;
		this.initialMeans = initialMeans;
	}


	//------------ SETTERS ------------\\

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public void setElements(Element[] elements) {
		this.elements = elements;
	}

	public void setNbClasses(int nbClasses) {
		this.nbClasses = nbClasses;
	}

	public void setInitialMeans(InitialMeans initialMeans) {
		this.initialMeans = initialMeans;
	}

	public void setDimensionsMinima(double[] dimensionsMinima) {
		this.dimensionsMinima = dimensionsMinima;
	}

	public void setDimensionsMaxima(double[] dimensionsMaxima) {
		this.dimensionsMaxima = dimensionsMaxima;
	}

	public void setDimensionsMinima(double dimensionsMinima, int nbDimensions) {
		this.dimensionsMinima = new double[nbDimensions];
		for (int d = 0; d < nbDimensions; ++d) {
			this.dimensionsMinima[d] = dimensionsMinima;
		}
	}

	public void setDimensionsMaxima(double dimensionsMaxima, int nbDimensions) {
		this.dimensionsMaxima = new double[nbDimensions];
		for (int d = 0; d < nbDimensions; ++d) {
			this.dimensionsMaxima[d] = dimensionsMaxima;
		}	
	}


	//------------ GETTERS ------------\\

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public Element[] getElements() {
		return elements;
	}

	public int getNbClasses() {
		return nbClasses;
	}

	public InitialMeans getInitialMeans() {
		return initialMeans;
	}

	public double[] getDimensionsMinima() {
		return dimensionsMinima;
	}

	public double[] getDimensionsMaxima() {
		return dimensionsMaxima;
	}
}
