package com.atompacman.klusterz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.atompacman.configuana.App;
import com.atompacman.configuana.Cmd;
import com.atompacman.configuana.StrictParam;
import com.atompacman.klusterz.Parameters.Misc;
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
import com.atompacman.toolkat.misc.StringHelper;

public final class Klusterz extends App {

    //====================================== CONSTANTS ===========================================\\

    private static final Logger logger = LogManager.getLogger(Klusterz.class);



    //======================================= FIELDS =============================================\\

    public Random randGen;



    //======================================= METHODS ============================================\\

    //---------------------------------------- INIT ----------------------------------------------\\

    public void init() {
        logger.info(StringHelper.title(getName() + " " + getVersion()));
        randGen = new Random(getDefaultProfile().getLong(Misc.RANDOM_SEED));
    }


    //--------------------------------------- EXECUTE --------------------------------------------\\

    public static List<KClass> execute(ClusteringPlan plan) {
        logger.info("Beginning clustering");

        int nbClasses = plan.getNbClasses();
        Element[] elements = plan.getElements();

        if (nbClasses < 1) {
            throw new IllegalArgumentException("The number of class must be a positive integer.");
        }

        if (elements.length == 0) {
            throw new IllegalArgumentException("The number of elements must not be zero.");
        }

        if (elements.length < nbClasses) {
            logger.warn("More classes than elements: Using trivial solution");
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
        return Arrays.asList(CartesianPlanClustering.class, ImageColorSegmentation.class);
    }

    public List<Class<? extends StrictParam>> getParamsClasses() {
        List<Class<? extends StrictParam>> paramClasses = new LinkedList<>();
        paramClasses.add(Parameters.class);
        return paramClasses;
    }


    //--------------------------------------- SHUTDOWN -------------------------------------------\\

    public void finalize() {

    }
}
