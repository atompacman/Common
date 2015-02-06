package com.atompacman.klusterz.app;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import com.atompacman.atomlog.Log;
import com.atompacman.configuana.Cmd;
import com.atompacman.configuana.CmdArgs;
import com.atompacman.configuana.CmdInfo;
import com.atompacman.configuana.Flag;
import com.atompacman.configuana.FlagInfo;
import com.atompacman.klusterz.Klusterz;
import com.atompacman.klusterz.Parameters.CPC;
import com.atompacman.klusterz.app.ImageColorSegmentation.ICSFlag;
import com.atompacman.klusterz.container.ClusteringPlan;
import com.atompacman.klusterz.container.ClusteringPlan.Algorithm;
import com.atompacman.klusterz.container.ClusteringPlan.InitialMeans;
import com.atompacman.klusterz.container.Element;
import com.atompacman.klusterz.container.KClass;

public class ImageColorSegmentation implements Cmd<Klusterz, ICSFlag> {
	
	//===================================== INNER TYPES ==========================================\\

	public enum ICSFlag implements Flag {
		ALGORITHM 					{ public FlagInfo info() { return new FlagInfo(
				"algo", "Clustering algorithm", 1, 
				"Define the clustering algorithm", 
				CPC.DEFAULT_ALGORITHM);
			}},
		INIT_MEANS_SELECTION 		{ public FlagInfo info() { return new FlagInfo(
				"ims", "Initial means selection", 1, 
				"Define how to select first means", 
				CPC.DEFAULT_INITIAL_MEANS);
			}},
		OUTPUT_IMAGE_PATH 			{ public FlagInfo info() { return new FlagInfo(
				"o", "Output image path", 1, 
				"Specify the path to final image", 
				"segmented_image.bmp");
			}};
	}
		
		
	private static final Algorithm 	  DEFAULT_ALGORITHM 	= Algorithm.K_MEANS;
	private static final InitialMeans DEFAULT_INITIAL_MEANS = InitialMeans.RANDOM;

	

	//======================================= METHODS ============================================\\

	//--------------------------------------- EXECUTE --------------------------------------------\\

	public void execute(Klusterz app, CmdArgs<ICSFlag> args) {
		String inputFile = args.getMainArgs().get(0);
		String outputFile = args.getValue(ICSFlag.OUTPUT_IMAGE_PATH);
		int nbClasses = Integer.parseInt(args.getMainArgs().get(1));
		Algorithm algo = Algorithm.valueOf(args.getValue(ICSFlag.ALGORITHM));
		InitialMeans initMeansSelect = InitialMeans.valueOf(
				args.getValue(ICSFlag.INIT_MEANS_SELECTION));

		segmentate(inputFile, outputFile, nbClasses, algo, initMeansSelect);
	}
	
	
	//------------------------------------- SEGMENTATE -------------------------------------------\\
	
	public static void segmentate(String inputFile, 
								  String outputFile, 
								  int nbClasses) {
		
		segmentate(inputFile, outputFile, nbClasses, DEFAULT_ALGORITHM, DEFAULT_INITIAL_MEANS);
	}
	
	public static void segmentate(String inputFile, 
			  					  String outputFile, 
			  					  int nbClasses,
			  					  Algorithm algorithm,
			  					  InitialMeans initialMeans) {
		
		segmentateAndGetClusters(inputFile, outputFile, nbClasses, algorithm, initialMeans);
	}
	
	protected static List<KClass> segmentateAndGetClusters(String inputFile, 
								  						   String outputFile, 
								  						   int nbClasses,
								  						   Algorithm algorithm,
								  						   InitialMeans initialMeans) {
		
		if (Log.infos() && Log.title("Image color segmentation application"));
		
		if (Log.infos() && Log.print("Loading image at \"" + inputFile + "\"."));
		BufferedImage inputImage = loadInputImage(inputFile);
		int nbPixels = inputImage.getWidth() * inputImage.getHeight();
		
		if (Log.infos() && Log.print("Reading pixels (" + nbPixels + " values)."));
		Element[] pixelElements = readPixelElements(inputImage);
		
		ClusteringPlan plan = new ClusteringPlan(algorithm, pixelElements, nbClasses, initialMeans);
		plan.setDimensionsMinima(0, 3);
		plan.setDimensionsMaxima(256, 3);

		List<KClass> clusters = Klusterz.execute(plan);
		
		if (Log.infos() && Log.print("Writing clustered image at \"" + outputFile + "\"."));
		writeClusteredImage(clusters, inputImage, outputFile);
		
		if (Log.infos() && Log.print("Done."));
		
		return clusters;
	}
	
	protected static BufferedImage loadInputImage(String inputFile) {
		try {
			File imageFile = new File(inputFile);
			if (!imageFile.exists()) {
				throw new FileNotFoundException("No such file as \"" + inputFile + "\".");
			}
			return ImageIO.read(imageFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected static Element[] readPixelElements(BufferedImage inputImage) {
		int nbPixels = inputImage.getWidth() * inputImage.getHeight();
		Element[] pixelElements = new Element[nbPixels];
		
		DataBuffer buffer = inputImage.getData().getDataBuffer();

		for (int i = 0; i < nbPixels; ++i) {
			double[] components = new double[3];
			components[2] = buffer.getElem(3 * i);
			components[1] = buffer.getElem(3 * i + 1);
			components[0] = buffer.getElem(3 * i + 2);
			pixelElements[i] = new Element(components);
		}
		
		return pixelElements;
	}

	protected static void writeClusteredImage(List<KClass> clusters, 
			BufferedImage inputImage, String outputFile) {
		
		if (outputFile == null) {
			throw new NullPointerException("Null output path.");
		}
		
		int width = inputImage.getWidth();
		
		for (KClass kClass : clusters) {
			double[] components = kClass.mean.components;

			int hexRGB = new Color((int) components[0], (int) components[1], 
					(int) components[2]).getRGB();

			for (Integer pixelIndex : kClass.elementsIndex) {
				inputImage.setRGB(pixelIndex % width, pixelIndex / width, hexRGB);
			}
		}
		
		try {
			ImageIO.write(inputImage, "bmp", new File(outputFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	//--------------------------------------- GETTERS --------------------------------------------\\

	public CmdInfo info() {
		return new CmdInfo("ics", "Image Color Segmentation", 2, "Reduce the "
				+ "number of different colors in an image using clustering");
	}
}
