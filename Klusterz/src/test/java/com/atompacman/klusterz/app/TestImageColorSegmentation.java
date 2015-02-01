package com.atompacman.klusterz.app;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.atompacman.klusterz.container.ClusteringPlan.Algorithm;
import com.atompacman.klusterz.container.ClusteringPlan.InitialMeans;
import com.atompacman.klusterz.container.KClass;

public class TestImageColorSegmentation {

	private static final String TEST_DIRECTORY = "data" + File.separator + "test" + File.separator;
		
	@Test
	public void assertConsistency() {
		String testFileName = TEST_DIRECTORY + "TheVanOfLove.png";
		List<KClass> a = null;
		List<KClass> b = null;
		
		try {
			a = ImageColorSegmentation.segmentateAndGetClusters(
					testFileName, "salut.bmp", 4, Algorithm.K_MEANS, InitialMeans.RANDOM);
		} catch (NullPointerException e) {
			
		}
		try {
			b = ImageColorSegmentation.segmentateAndGetClusters(
					testFileName, null, 10, Algorithm.K_MEANS, InitialMeans.RANDOM);
			
		} catch (NullPointerException e) {
			
		}		
		assertEquals(a, b);
	}
}
