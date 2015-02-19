package com.atompacman.klusterz.app;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atompacman.atomlog.Log;
import com.atompacman.klusterz.Parameters.Paths;
import com.atompacman.klusterz.container.ClusteringPlan.Algorithm;
import com.atompacman.klusterz.container.ClusteringPlan.InitialMeans;
import com.atompacman.toolkat.io.IO;
import com.atompacman.toolkat.test.AbstractTest;

public class TestImageColorSegmentation extends AbstractTest  {

	//===================================== BEFORE CLASS =========================================\\

	@BeforeClass
	public static void beforeClass() {
		new Log().init();
		detectTestDirectory(Paths.TEST_DIRECTORY, Paths.APP_PACKAGE);
	}



	//================================== FUNCTIONNAL TESTS =======================================\\

	@Test
	public void completeTest() {
		ImageColorSegmentation.segmentateAndGetClusters(IO.getPath(testDir, "Jellyfish.jpg"), 
				IO.getPath(testDir, "Jellyseg.jpg"), 4, Algorithm.K_MEANS, InitialMeans.RANDOM);

	}
}
