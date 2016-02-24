package com.atompacman.klusterz.app;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
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
import com.atompacman.klusterz.app.ImageColorSegmentation.ICSFlag;
import com.atompacman.klusterz.container.ClusteringPlan;
import com.atompacman.klusterz.container.ClusteringPlan.Algorithm;
import com.atompacman.klusterz.container.ClusteringPlan.InitialMeans;
import com.atompacman.toolkat.StringUtils;
import com.atompacman.klusterz.container.Element;
import com.atompacman.klusterz.container.KClass;

public class ImageColorSegmentation implements Cmd<Klusterz, ICSFlag> {

    //===================================== INNER TYPES ==========================================\\

    public enum ICSFlag implements Flag {
        ALGORITHM                   { public FlagInfo info() { return new FlagInfo(
                "algo", "Clustering algorithm", 1, 
                "Define the clustering algorithm", 
                CPC.DEFAULT_ALGORITHM);
        }},
        INIT_MEANS_SELECTION        { public FlagInfo info() { return new FlagInfo(
                "ims", "Initial means selection", 1, 
                "Define how to select first means", 
                CPC.DEFAULT_INITIAL_MEANS);
        }},
        OUTPUT_IMAGE_PATH           { public FlagInfo info() { return new FlagInfo(
                "o", "Output image path", 1, 
                "Specify the path to final image", 
                "segmented_image.bmp");
        }};
    }



    //======================================= FIELDS =============================================\\

    private static final Logger logger = LogManager.getLogger(ImageColorSegmentation.class);

    private static final Algorithm    DEFAULT_ALGORITHM     = Algorithm.K_MEANS;
    private static final InitialMeans DEFAULT_INITIAL_MEANS = InitialMeans.RANDOM;



    //======================================= METHODS ============================================\\

    //--------------------------------------- EXECUTE --------------------------------------------\\

    public void execute(Klusterz app, CmdArgs<ICSFlag> args) {
        segmentate(new File            (args.getMainArgs().get(0)), 
                   new File            (args.getValue(ICSFlag.OUTPUT_IMAGE_PATH)), 
                   Integer.parseInt    (args.getMainArgs().get(1)), 
                   Algorithm.valueOf   (args.getValue(ICSFlag.ALGORITHM)), 
                   InitialMeans.valueOf(args.getValue(ICSFlag.INIT_MEANS_SELECTION)));
    }


    //------------------------------------- SEGMENTATE -------------------------------------------\\

    public static void segmentate(File inImg,  File outImg, int nbClasses) {
        segmentate(inImg, outImg, nbClasses, DEFAULT_ALGORITHM, DEFAULT_INITIAL_MEANS);
    }

    public static void segmentate(File        inImg, 
                                 File         outImg, 
                                 int          numClasses,
                                 Algorithm    algorithm,
                                 InitialMeans initialMeans) {

        segmentateAndGetClusters(inImg, outImg, numClasses, algorithm, initialMeans);
    }

    static List<KClass> segmentateAndGetClusters(File         inImg,
                                                 File         outImg,
                                                 int          numClasses,
                                                 Algorithm    algorithm,
                                                 InitialMeans initialMeans) {

        logger.info(StringUtils.title("Image color segmentation application"));
        logger.info("Loading image at \"{}\".", inImg.getPath());

        BufferedImage referenceImg;
        try {
            referenceImg = ImageIO.read(inImg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int nbPixels = referenceImg.getWidth() * referenceImg.getHeight();

        logger.info("Reading pixels ({} values).", nbPixels);
        Element[] pixelElements = readPixelElements(referenceImg);

        ClusteringPlan plan = new ClusteringPlan(algorithm, pixelElements, numClasses,initialMeans);
        plan.setDimensionsMinima(0, 3);
        plan.setDimensionsMaxima(256, 3);

        List<KClass> clusters = Klusterz.execute(plan);

        logger.info("Writing clustered image at \"{}\".", outImg.getPath());
        writeClusteredImage(clusters, referenceImg, outImg);

        logger.info("Done");

        return clusters;
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
            BufferedImage clusteredImg, File outImg) {

        int width = clusteredImg.getWidth();

        for (KClass kClass : clusters) {
            double[] components = kClass.mean.components;

            int hexRGB = new Color((int) components[0], (int) components[1], 
                    (int) components[2]).getRGB();

            for (Integer pixelIndex : kClass.elementsIndex) {
                clusteredImg.setRGB(pixelIndex % width, pixelIndex / width, hexRGB);
            }
        }

        try {
            ImageIO.write(clusteredImg, "bmp", outImg);
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
