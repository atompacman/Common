package com.atompacman.klusterz.algorithm;

import java.util.Arrays;

import com.atompacman.klusterz.container.Element;
import com.atompacman.klusterz.container.KClass;
import com.atompacman.klusterz.initialMeans.InitialMeansSelection;

public class KMeans extends ClusteringAlgorithm {

    //======================================= METHODS ============================================\\

    //---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

    public KMeans(int                   numClasses,
                  Element[]             elements,
                  InitialMeansSelection initialMeansSelector) {

        super(numClasses, elements, initialMeansSelector);
    }


    //--------------------------------------- EXECUTE --------------------------------------------\\

    public boolean updateClassesUntilConvergence() {
        boolean convergence = true;

        double[] updatedMean = new double[numDim];

        for (KClass kClass : classes) {
            if (kClass.elementsIndex.isEmpty()) {
                continue;
            }
            Arrays.fill(updatedMean, 0);

            for (int elementIndex : kClass.elementsIndex) {
                Element element = elements[elementIndex];

                for (int n = 0; n < numDim; ++n) {
                    updatedMean[n] += element.components[n];
                }
            }
            double meanFactor = 1.0 / (double) kClass.elementsIndex.size();

            for (int n = 0; n < numDim; ++n) {
                updatedMean[n] *= meanFactor;
            }
            Element newMean = new Element(updatedMean);

            if (!kClass.mean.equals(newMean)) {
                convergence = false;
                kClass.mean = new Element(newMean);
            }
        }
        return convergence;
    }
}
