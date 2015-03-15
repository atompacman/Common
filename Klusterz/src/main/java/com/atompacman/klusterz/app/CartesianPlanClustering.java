package com.atompacman.klusterz.app;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.atompacman.configuana.Cmd;
import com.atompacman.configuana.CmdArgs;
import com.atompacman.configuana.CmdInfo;
import com.atompacman.configuana.Flag;
import com.atompacman.configuana.FlagInfo;
import com.atompacman.klusterz.Klusterz;
import com.atompacman.klusterz.Parameters.CPC;
import com.atompacman.klusterz.app.CartesianPlanClustering.CPCFlag;
import com.atompacman.klusterz.container.ClusteringPlan;
import com.atompacman.klusterz.container.ClusteringPlan.Algorithm;
import com.atompacman.klusterz.container.ClusteringPlan.InitialMeans;
import com.atompacman.klusterz.container.Element;
import com.atompacman.klusterz.container.KClass;
import com.atompacman.toolkat.exception.Throw;
import com.atompacman.toolkat.io.IO;
import com.atompacman.toolkat.misc.StringHelper;

public class CartesianPlanClustering implements Cmd<Klusterz, CPCFlag> {
	
	//===================================== INNER TYPES ==========================================\\

	public enum CPCFlag implements Flag {
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
				"clustered_image.bmp");
			}},
		PTS_SIZE_ON_FINAL_IMAGE 	{ public FlagInfo info() { return new FlagInfo(
				"pt_size", "Points size on final image", 1, 
				"Define the size of the points on the final image", 
				Integer.toString(CPC.DEFAULT_PT_SIZE_ON_IMG));
			}},
		MEANS_SIZE_ON_FINAL_IMAGE 	{ public FlagInfo info() { return new FlagInfo(
				"mean_size", "Means points size on final image", 1, 
				"Define the size of the mean points on the final image", 
				Integer.toString(CPC.DEFAULT_MEAN_PT_SIZE_ON_IMG));
			}},	
		FINAL_IMAGE_SIZE_WIDTH 		{ public FlagInfo info() { return new FlagInfo(
				"w", "Final image width", 1, 
				"Define the length of the final width", 
				Integer.toString(CPC.DEFAULT_IMG_WIDTH));
			}},	
		FINAL_IMAGE_SIZE_HEIGHT 	{ public FlagInfo info() { return new FlagInfo(
				"h", "Final image height", 1, 
				"Define the length of the final height", 
				Integer.toString(CPC.DEFAULT_IMG_HEIGHT));
			}};
	}

	

	//====================================== CONSTANTS ===========================================\\

	private static final Logger logger = LogManager.getLogger(CartesianPlanClustering.class);

	// Macros
	private static final String CP 		= "Cartesian plan";
	private static final String	CPDF 	= CP + " description file";
	private static final int	X		= 0;
	private static final int	Y		= 1;

	
	
	//======================================= FIELDS =============================================\\

	// Algorithm parameters
	private Algorithm clusterAlgo;
	private InitialMeans initMeansSelect;
	
	// Cartesian plan
	private Element[] points;
	private double[] planMinima;
	private double[] planMaxima;
	private boolean cartesianPlanRead;

	// Results
	private List<KClass> clusters;
	private boolean planClustered;
	private int resImgPtSize;
	private int resImgMeanSize;
	private double coordToPixelX;
	private double coordToPixelY;
	
	
	
	//======================================= METHODS ============================================\\

	//---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

	public CartesianPlanClustering() {
		this.clusterAlgo = Algorithm.valueOf(CPC.DEFAULT_ALGORITHM);
		this.initMeansSelect = InitialMeans.valueOf(CPC.DEFAULT_INITIAL_MEANS);
		
		this.points 			= null;
		this.planMinima 		= new double[2];
		this.planMaxima			= new double[2];
		this.cartesianPlanRead 	= false;
		
		this.clusters 			= null;
		this.planClustered 		= false;
		this.resImgPtSize 		= CPC.DEFAULT_PT_SIZE_ON_IMG;
		this.resImgMeanSize 	= CPC.DEFAULT_MEAN_PT_SIZE_ON_IMG;
		this.coordToPixelX 		= 0;
		this.coordToPixelY 		= 0;
	}


	//--------------------------------------- EXECUTE --------------------------------------------\\
	
	public void execute(Klusterz app, CmdArgs<CPCFlag> args) {
		logger.info(StringHelper.title(CP + " clustering application", 1));

		// Read Cartesian plan
		clusterAlgo 	= Algorithm.valueOf(args.getValue(CPCFlag.ALGORITHM));
		initMeansSelect = InitialMeans.valueOf(args.getValue(CPCFlag.INIT_MEANS_SELECTION));
		resImgPtSize 	= Integer.parseInt(args.getValue(CPCFlag.PTS_SIZE_ON_FINAL_IMAGE));
		resImgMeanSize 	= Integer.parseInt(args.getValue(CPCFlag.MEANS_SIZE_ON_FINAL_IMAGE));
		
		String cartesianPlanDescPath = args.getMainArgs().get(0);
		
		try {
			readCartesianPlanFile(IO.getFile(cartesianPlanDescPath));
		} catch (Exception e) {
			throw new RuntimeException("Could not read cartesian plan "
					+ "file at \"" + cartesianPlanDescPath + "\".", e);
		}
		
		// Cluster
		int nbClasses = Integer.parseInt(args.getMainArgs().get(1));
		
		try {
			cluster(nbClasses);
		} catch (ClusteringAppException e) {
			throw new RuntimeException("Clustering algorithm failed.", e);
		}
		
		// Write result image
		String outputFile = args.getValue(CPCFlag.OUTPUT_IMAGE_PATH);
		int imgWidth = Integer.parseInt(args.getValue(CPCFlag.FINAL_IMAGE_SIZE_WIDTH));
		int imgHeight = Integer.parseInt(args.getValue(CPCFlag.FINAL_IMAGE_SIZE_HEIGHT));
		
		try {
			writeResultImage(IO.getFile(outputFile), new Dimension(imgWidth, imgHeight));
		} catch (Exception e) {
			throw new RuntimeException("Could not save final image at \"" + outputFile + "\".", e);
		}
	}
	

	//------------------------------- READ CARTESIAN PLAN FILE -----------------------------------\\

	public void readCartesianPlanFile(File cartesianPlanDesc) throws ClusteringAppException {
		if (cartesianPlanDesc == null) {
			Throw.a(ClusteringAppException.class, CPDF + " is null.");
		}
		
		logger.info("Reading {} at \"{}\".", CPDF, cartesianPlanDesc.getAbsolutePath());
		
		List<String> fileLines = readFileLines(cartesianPlanDesc);
		try {
			parseCartesianPlanLimits(fileLines);
			parse2DPoints(fileLines);
		} catch (Exception e) {
			Throw.a(ClusteringAppException.class, CPDF + " format is invalid: ", e);
		}
		
		cartesianPlanRead = true;
		planClustered = false;
	}

	private List<String> readFileLines(File cartesianPlanDesc) throws ClusteringAppException {
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(cartesianPlanDesc));
			String line;

			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (Exception e) {
			Throw.a(ClusteringAppException.class, "Could not parse " + CPDF + ": ", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				Throw.a(ClusteringAppException.class, "Could not close file reader.", e);
			}
		}

		return lines;
	}

	private void parseCartesianPlanLimits(List<String> fileLines) throws ClusteringAppException {
		if (fileLines.isEmpty()) {
			Throw.a(ClusteringAppException.class, CPDF + " is empty.");
		}
		if (fileLines.size() == 1) {
			Throw.a(ClusteringAppException.class, CPDF + " must at least contain one point.");
		}
		String firstLine = fileLines.get(0);
		String[] parts = firstLine.split(" ");

		if (parts.length != 4) {
			Throw.a(ClusteringAppException.class, "First line in " + CPDF + " must be made of 4 values "
					+ "representing the bottom-left and upper-right limits of the plan.");
		}

		planMinima = new double[2];
		planMaxima = new double[2];
		planMinima[X] = Integer.parseInt(parts[0]);
		planMinima[Y] = Integer.parseInt(parts[1]);
		planMaxima[X] = Integer.parseInt(parts[2]);
		planMaxima[Y] = Integer.parseInt(parts[3]);
				
		if (planMinima[X] >= planMaxima[X] || planMinima[Y] >= planMaxima[Y]) {
			Throw.a(ClusteringAppException.class, "Minimum and maximum are swapped.");
		}
	}

	private void parse2DPoints(List<String> fileLines) throws ClusteringAppException {
		points = new Element[fileLines.size() - 1];

		for (int i = 1; i < fileLines.size(); ++i) {
			String[] parts = fileLines.get(i).split(" ");
			if (parts.length != 2) {
				Throw.a(ClusteringAppException.class, "Lines in " + CPDF + " must "
						+ "be made of two numbers separated by spaces.");
			}
			double[] coord = new double[2];
			coord[X] = Integer.parseInt(parts[0]);
			coord[Y] = Integer.parseInt(parts[1]);

			if (coord[X] > planMaxima[X] || coord[X] < planMinima[X] ||
				coord[Y] > planMaxima[Y] || coord[Y] < planMinima[Y]) {
				Throw.a(ClusteringAppException.class, "Point at line " + i + " is outside limits.");
			}
			
			points[i - 1] = new Element(coord);
		}
	}


	//--------------------------------------- CLUSTER --------------------------------------------\\

	public void cluster(int nbClusters) throws ClusteringAppException {
		logger.info("Clustering {}", CP);

		if (!cartesianPlanRead) {
			Throw.a(ClusteringAppException.class, "A " + CPDF + " must be read before clustering.");
		}
		if (nbClusters < 1) {
			Throw.a(ClusteringAppException.class, "Nb of clusters must be a positive integer.");
		}
		
		ClusteringPlan plan = new ClusteringPlan(clusterAlgo, points, nbClusters, initMeansSelect);
		plan.setDimensionsMinima(planMinima);
		plan.setDimensionsMaxima(planMaxima);
		
		clusters = Klusterz.execute(plan);
		
		planClustered = true;
	}
	

	//---------------------------------- WRITE RESULT IMAGE --------------------------------------\\

	public void writeResultImage(File resImg) throws ClusteringAppException {
		writeResultImage(resImg, new Dimension(CPC.DEFAULT_IMG_WIDTH, CPC.DEFAULT_IMG_HEIGHT));
	}

	public void writeResultImage(File resImg, Dimension dim) throws ClusteringAppException {
		if (!planClustered) {
			Throw.a(ClusteringAppException.class, CP + " clustering must "
					+ "be completed before writing result image.");
		}
		
		logger.info("Writing result image at \"{}\"", resImg.getAbsolutePath());

		WritableRaster raster = WritableRaster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE, dim.width, dim.height, 3, new Point(0,0));
	
		coordToPixelX = (double) dim.width / (planMaxima[X] - planMinima[X]);
		coordToPixelY = (double) dim.height / (planMaxima[Y] - planMinima[Y]);

		int[][] colors = generateColors();
		
		for (int i = 0; i < clusters.size(); ++i) {
			KClass kClass = clusters.get(i);
			int[] color = colors[i];
			
			for (Integer pointIndex : kClass.elementsIndex) {
				double[] coord = points[pointIndex].components;
				addRectangleToRegion(coord, raster, resImgPtSize, color);
			}

			for (int j = 0; j < 3; ++j) {
				color[j] *= CPC.MEAN_COLOR_DARK_COEFF;
			}
			
			double[] meanCoord = kClass.mean.components;
			addRectangleToRegion(meanCoord, raster, resImgMeanSize, color);
		}
		
		fillRegions(raster, colors);

		BufferedImage image = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
		image.setData(raster);
		
		try {			
			ImageIO.write(image, "bmp", resImg);
		} catch (IOException e) {
			Throw.a(ClusteringAppException.class, "Could not write final image", e);
		}
		logger.info("Done writing image");
	}
	
	private int[][] generateColors() {
		int[][] colors = new int[clusters.size()][3];
		double increm = 1.0 / (double) clusters.size();
		double value = 0;
		
		for (int i = 0; i < clusters.size(); ++i) {
			colors[i] = doubleToSaturatedColor(value);
			value += increm;
		}
		return colors;
	}
	
	private static int[] doubleToSaturatedColor(double value) {
		double[] rgb = new double[3];
		value *= 6;
		value %= 6;
		
		if (value < 1) {
			rgb[0] = 1;
			rgb[1] = value;
		} else if (value < 2) {
			rgb[0] = 2 - value;
			rgb[1] = 1;
		} else if (value < 3) {
			rgb[1] = 1;
			rgb[2] = value - 2;
		} else if (value < 4) {
			rgb[1] = 4 - value;
			rgb[2] = 1;
		} else if (value < 5) {
			rgb[0] = value - 4;
			rgb[2] = 1;
		} else {
			rgb[0] = 1;
			rgb[2] = 6 - value;
		}

		int[] color = new int[3];
		
		for (int i = 0; i < 3; ++i) {
			color[i] = (int) (rgb[i] * 255.0);
		}
		
		return color;
	}
	
	private void addRectangleToRegion(double[] cartCoord,  
			WritableRaster raster, int ptSize, int[] color) {
		
		int[] upperLeftPxl = new int[2];
		int[] lowerRightPxl = new int[2];
		
		upperLeftPxl[X] = (int) Math.rint(Math.max(0, coordToPixelX * cartCoord[X] - ptSize * 0.5));
		upperLeftPxl[Y] = (int) Math.rint(Math.max(0, coordToPixelY * cartCoord[Y] - ptSize * 0.5));
		lowerRightPxl[X] = Math.min(upperLeftPxl[X] + ptSize, raster.getWidth() - 1);
		lowerRightPxl[Y] = Math.min(upperLeftPxl[Y] + ptSize, raster.getHeight() - 1);

		int regionWidth = lowerRightPxl[X] - upperLeftPxl[X];
		int regionHeight = lowerRightPxl[Y] - upperLeftPxl[Y];
		int nbPxlInRegion = regionWidth * regionHeight;
		
		int[] colorArray = new int[nbPxlInRegion * 3];
		for (int i = 0; i < nbPxlInRegion; ++i) {
			System.arraycopy(color, 0, colorArray, i * 3, 3);
		}
		int[] border = new int[nbPxlInRegion * 3];
		Arrays.fill(border, CPC.PT_BORDER_COLOR);
		raster.setPixels(upperLeftPxl[X], upperLeftPxl[Y], regionWidth, regionHeight, border);
		raster.setPixels(upperLeftPxl[X] + 1, upperLeftPxl[Y] + 1, 
				regionWidth - 2, regionHeight - 2, colorArray);
	}

	private void fillRegions(WritableRaster raster, int[][] colors) {
		double x2 = (planMaxima[X] - planMinima[X]) * (planMaxima[X] - planMinima[X]);
		double y2 = (planMaxima[Y] - planMinima[Y]) * (planMaxima[Y] - planMinima[Y]);
		double distFactor = 1.0 / Math.sqrt(x2 + y2);
		
		for (int x = 0; x < raster.getWidth(); ++x) {
			for (int y = 0; y < raster.getHeight(); ++y) {
				double[] pixel = raster.getPixel(x, y, (double[]) null);
				if (pixel[0] != 0.0 || pixel[1] != 0.0 || pixel[2] != 0.0) {
					continue;
				}
				
				double minDist = Double.MAX_VALUE;
				int closestClassIndex = -1;
				for (int i = 0; i < clusters.size(); ++i) {
					double xCoord = (double) x / coordToPixelX;
					double yCoord = (double) y / coordToPixelY;
					Element pt = new Element(new double[]{xCoord, yCoord});
					double dist = clusters.get(i).mean.squareEuclidDistFrom(pt);
					if (dist < minDist) {
						minDist = dist;
						closestClassIndex = i;
					}
				}
				
				for (int i = 0; i < 3; ++i) {
					int comp = colors[closestClassIndex][i];
					pixel[i] = comp * (1 - Math.pow(Math.sqrt(minDist) * distFactor, 0.2));
				}
				raster.setPixel(x, y, pixel);
			}
		}
	}
	
	
	//--------------------------------------- SETTERS --------------------------------------------\\

	public void setClusteringAlgorithm(Algorithm algo) {
		this.clusterAlgo = algo;
	}
	
	public void setInitMeansSelection(InitialMeans initMeansSelect) {
		this.initMeansSelect = initMeansSelect;
	}
	
	
	//--------------------------------------- GETTERS --------------------------------------------\\

	public Algorithm getClusteringAlgorithm() {
		return clusterAlgo;
	}

	public InitialMeans getInitMeansSelection() {
		return initMeansSelect;
	}

	public List<KClass> getClusters() {
		if (!planClustered) {
			throw new IllegalArgumentException(CP + " was not clustered yet.");
		}
		return clusters;
	}

	public CmdInfo info() {
		return new CmdInfo("cpc", "Cartesian Plan Clustering", 2, 
				"Clusters 2D points in a cartesian plan");
	}
}
