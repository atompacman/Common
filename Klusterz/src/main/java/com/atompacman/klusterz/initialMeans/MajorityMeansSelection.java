package com.atompacman.klusterz.initialMeans;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.atompacman.klusterz.container.Element;
import com.atompacman.klusterz.container.KClass;

public final class MajorityMeansSelection extends InitialMeansSelection {

    //======================================= METHODS ============================================\\

    //---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

    public MajorityMeansSelection(int nbClasses) {
        super(nbClasses);
    }


    //--------------------------------- SELECT INITIAL MEANS -------------------------------------\\

    public KClass[] selectInitialMeans(Element[] elements) {
        Map<Element, Integer> histogram = new HashMap<Element, Integer>();

        for (Element element : elements) {
            Integer count = histogram.get(element);
            if (count == null) {
                histogram.put(element, 1);
            } else {
                histogram.put(element, count + 1);
            }
        }

        KClass[] classes = new KClass[numClasses];

        for (int i = 0; i < numClasses; ++i) {
            int max = 0;
            Element mostCommon = null;

            for (Entry<Element, Integer> entry : histogram.entrySet()) {
                int count = entry.getValue();
                if (count > max) {
                    max = count;
                    mostCommon = entry.getKey();
                }
            }
            histogram.remove(mostCommon);

            classes[i] = new KClass(mostCommon);
        }

        return classes;
    }
}
