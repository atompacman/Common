package com.atompacman.klusterz.app;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

import com.atompacman.klusterz.container.ClusteringPlan.Algorithm;
import com.atompacman.klusterz.container.ClusteringPlan.InitialMeans;
import com.atompacman.toolkat.test.AbstractTest;

public class TestCartesianPlanClustering extends AbstractTest {

    //===================================== SYSTEM TESTS =========================================\\

    @Test
    public void completeTest() throws ClusteringAppException, FileNotFoundException {
        CartesianPlanClustering cpc = new CartesianPlanClustering();
        cpc.setClusteringAlgorithm(Algorithm.K_MEANS);
        cpc.setInitMeansSelection(InitialMeans.RANDOM);

        File testFile = loadResources("completeTest.txt");
        cpc.readCartesianPlanFile(testFile);

        for (int i = 0; i < 5; ++i) {
            cpc.cluster(5);

            File resImg = new File(testFile.getParentFile(), "completeTest_" + i + ".png");
            cpc.writeResultImage(resImg, new Dimension(300, 300));
            resImg.delete();
        }
    }
}