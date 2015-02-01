package com.atompacman.klusterz.app;

import java.awt.Dimension;
import java.io.File;

import org.junit.Test;

import com.atompacman.klusterz.Parameters;
import com.atompacman.klusterz.container.ClusteringPlan.Algorithm;
import com.atompacman.klusterz.container.ClusteringPlan.InitialMeans;
import com.atompacman.toolkat.test.TestFileDetector;

public class TestCartesianPlanClustering {
	
	@Test
	public void completeTest() throws ClusteringAppException {
		CartesianPlanClustering cpc = new CartesianPlanClustering();
		cpc.setClusteringAlgorithm(Algorithm.K_MEANS);
		cpc.setInitMeansSelection(InitialMeans.RANDOM);
		
		cpc.readCartesianPlanFile(TestFileDetector.detectSingleFileForCurrentTest());
		
		for (int i = 0; i < 5; ++i) {
			cpc.cluster(5);
			
			String outputPath = Parameters.Paths.RESULTS_DIRECTORY;
			outputPath = outputPath + File.separator + "completeTest_" + i + ".png";
			cpc.writeResultImage(outputPath, new Dimension(300, 300));
		}
	}
}
