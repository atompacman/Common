package com.atompacman.klusterz.app;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atompacman.atomlog.Log;
import com.atompacman.klusterz.Parameters.Paths;
import com.atompacman.klusterz.container.ClusteringPlan.Algorithm;
import com.atompacman.klusterz.container.ClusteringPlan.InitialMeans;
import com.atompacman.toolkat.test.AbstractTest;

public class TestImageColorSegmentation extends AbstractTest  {

	//===================================== BEFORE CLASS =========================================\\

	@BeforeClass
	public static void beforeClass() {
		Log.quickInit();
		detectTestDirectory(Paths.TEST_DIRECTORY, Paths.APP_PACKAGE);
	}



	//================================== FUNCTIONNAL TESTS =======================================\\

	@Test
	public void completeTest() throws FileNotFoundException {
		File outImg = new File(testDir, "Jellyseg.jpg");
		ImageColorSegmentation.segmentateAndGetClusters(new File(testDir, "Jellyfish.jpg"), 
				outImg, 5, Algorithm.K_MEANS, InitialMeans.RANDOM);
		outImg.delete();
	}
}
