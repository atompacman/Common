package com.atompacman.klusterz.initialMeans;

import java.util.Random;

import com.atompacman.klusterz.container.Element;
import com.atompacman.klusterz.container.KClass;

public final class RandomMeansSelection extends InitialMeansSelection {

    //======================================= FIELDS =============================================\\

    private Random         randGen;
    private final double[] dimMinima;
    private final double[] dimMaxima;



    //======================================= METHODS ============================================\\

    //---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

    public RandomMeansSelection(int nbClasses, double[] dimMinima, double[] dimMaxima) {
        super(nbClasses);
        this.randGen = new Random();

        if (dimMinima.length != dimMaxima.length) {
            throw new IllegalArgumentException("Dimensions of minima (" + dimMinima.length + 
                    ") does not match dimensions of maxima (" + dimMaxima.length + ").");
        }
        for (int i = 0; i < dimMinima.length; ++i) {
            if (dimMinima[i] > dimMaxima[i]) {
                throw new IllegalArgumentException("Minimum of dimension " + 
                        (i + 1) + " (" + dimMinima[i] + ") is smaller "
                        + "than its maximum (" + dimMaxima[i] + ").");
            }
        }

        this.dimMinima = dimMinima;
        this.dimMaxima = dimMaxima;
    }


    //--------------------------------- SELECT INITIAL MEANS -------------------------------------\\

    public KClass[] selectInitialMeans(Element[] elements) {
        KClass[] classes = new KClass[numClasses];
        int nbDimensions = dimMinima.length;

        for (int i = 0; i < numClasses; ++i) {
            double[] components = new double[nbDimensions];
            for (int d = 0; d < nbDimensions; ++d) {
                components[d] = dimMinima[d] + randGen.nextDouble() * (dimMaxima[d] - dimMinima[d]);
            }
            classes[i] = new KClass(new Element(components));
        }

        return classes;
    }
}
