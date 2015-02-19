package com.atompacman.klusterz.app;

import java.awt.Dimension;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atompacman.atomlog.Log;
import com.atompacman.klusterz.Parameters;
import com.atompacman.klusterz.Parameters.Paths;
import com.atompacman.klusterz.container.ClusteringPlan.Algorithm;
import com.atompacman.klusterz.container.ClusteringPlan.InitialMeans;
import com.atompacman.toolkat.io.IO;
import com.atompacman.toolkat.test.AbstractTest;
import com.atompacman.toolkat.test.TestFileDetector;

public class TestCartesianPlanClustering extends AbstractTest {
	
	//===================================== BEFORE CLASS =========================================\\

	@BeforeClass
	public static void beforeClass() {
		new Log().init();
		detectTestDirectory(Paths.TEST_DIRECTORY, Paths.APP_PACKAGE);
	}
	
	
	
	//================================== FUNCTIONNAL TESTS =======================================\\

	@Test
	public void completeTest() throws ClusteringAppException {
		CartesianPlanClustering cpc = new CartesianPlanClustering();
		cpc.setClusteringAlgorithm(Algorithm.K_MEANS);
		cpc.setInitMeansSelection(InitialMeans.RANDOM);
		
		cpc.readCartesianPlanFile(TestFileDetector.detectSingleFileForCurrentTest());
		
		for (int i = 0; i < 5; ++i) {
			cpc.cluster(5);
			
			String outputPath = Parameters.Paths.RESULTS_DIRECTORY;
			outputPath = IO.getPath(testDir, "completeTest_" + i + ".png");
			cpc.writeResultImage(outputPath, new Dimension(300, 300));
		}
	}
}
