package com.atompacman.klusterz.container;

import com.atompacman.klusterz.Parameters;

public final class ClusteringPlan {

    //===================================== INNER TYPES ==========================================\\

    public enum Algorithm { 
        K_MEANS   (InitialMeans.RANDOM, InitialMeans.MAJORITY), 
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



    //======================================= FIELDS =============================================\\

    private Algorithm algorithm;
    private Element[] elements;
    private int       numClasses;

    private InitialMeans initialMeans;
    private double[]     minDim; 
    private double[]     maxDim;



    //======================================= METHODS ============================================\\

    //---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

    public ClusteringPlan(Algorithm algorithm, 
                          Element[] elements, 
                          int       numClasses) {

        this(algorithm, 
             elements, 
             numClasses, 
             InitialMeans.valueOf(Parameters.CPC.DEFAULT_INITIAL_MEANS));
    }

    public ClusteringPlan(Algorithm    algorithm, 
                          Element[]    elements, 
                          int          numClasses, 
                          InitialMeans initialMeans) {

        this.algorithm    = algorithm;
        this.elements     = elements;
        this.numClasses   = numClasses;
        this.initialMeans = initialMeans;
    }


    //--------------------------------------- SETTERS --------------------------------------------\\

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setElements(Element[] elements) {
        this.elements = elements;
    }

    public void setNbClasses(int nbClasses) {
        this.numClasses = nbClasses;
    }

    public void setInitialMeans(InitialMeans initialMeans) {
        this.initialMeans = initialMeans;
    }

    public void setDimensionsMinima(double[] dimensionsMinima) {
        this.minDim = dimensionsMinima;
    }

    public void setDimensionsMaxima(double[] dimensionsMaxima) {
        this.maxDim = dimensionsMaxima;
    }

    public void setDimensionsMinima(double dimensionsMinima, int nbDimensions) {
        this.minDim = new double[nbDimensions];
        for (int d = 0; d < nbDimensions; ++d) {
            this.minDim[d] = dimensionsMinima;
        }
    }

    public void setDimensionsMaxima(double dimensionsMaxima, int nbDimensions) {
        this.maxDim = new double[nbDimensions];
        for (int d = 0; d < nbDimensions; ++d) {
            this.maxDim[d] = dimensionsMaxima;
        }	
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public Element[] getElements() {
        return elements;
    }

    public int getNbClasses() {
        return numClasses;
    }

    public InitialMeans getInitialMeans() {
        return initialMeans;
    }

    public double[] getDimensionsMinima() {
        return minDim;
    }

    public double[] getDimensionsMaxima() {
        return maxDim;
    }
}
