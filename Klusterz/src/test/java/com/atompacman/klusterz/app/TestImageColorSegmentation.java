package com.atompacman.klusterz.app;

import java.io.File;

import org.junit.Test;

import com.atompacman.klusterz.container.ClusteringPlan.Algorithm;
import com.atompacman.klusterz.container.ClusteringPlan.InitialMeans;
import com.atompacman.toolkat.test.AbstractTest;

public class TestImageColorSegmentation extends AbstractTest  {

    //===================================== SYSTEM TESTS =========================================\\

    @Test
    public void completeTest() {
        File inImg = loadResource("Jellyfish.jpg");
        File outImg = new File(inImg.getParentFile(), "Jellyseg.jpg");
        ImageColorSegmentation.segmentateAndGetClusters(inImg, outImg, 
                5, Algorithm.K_MEANS, InitialMeans.RANDOM);
        outImg.delete();
    }
}
