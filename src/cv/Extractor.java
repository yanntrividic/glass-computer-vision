package cv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import io.Reader;
import ui.Window;

/**
 * Static class with methods to extract characteristics from images.
 * 
 * @author Yann Trividic
 * @version 1.0
 */
public class Extractor {
	public static Mat computeImage(int stage, Mat mat, int medianFilterKSize, double alphaSrc, double alphaMask,
			double intensityThreshold, double contourThreshold, double minimumSurface, int thresholdVesselContour,
			int kernelVesselContour) {

		// System.out.println("medianFilterKSize="+medianFilterKSize+"\n"+
		// "alphaSrc="+alphaSrc+"\n"+
		// "alphaMask="+alphaMask+"\n"+
		// "intensityThreshold="+intensityThreshold+"\n"+
		// "contourThreshold="+contourThreshold+"\n"+
		// "minimumSurface="+minimumSurface+"\n") ;

		Mat mask = Imgcodecs.imread(Reader.getResourcesDir() + "gaussian_distribution.jpg");
		mask = PreProcessing.rgbToGrayScale(mask);

		Mat testImg = PreProcessing.resizeSpecifiedWidth(mat, 500);
		Mat grayScale = PreProcessing.rgbToGrayScale(testImg);
		grayScale = PreProcessing.medianFilter(grayScale, medianFilterKSize);

		testImg = cv.ProcessingUtils.applyMask(grayScale, alphaSrc, mask, alphaMask, 0.0);

		Point[] points = SpecularReflexion.findSpecularReflexion(testImg, intensityThreshold, contourThreshold);

		Mat croppedImg = cv.ProcessingUtils.getCroppedImageFromTopLeftBotRight(grayScale, points[0], points[1], minimumSurface);
		
		if(stage == Window.CROPPING_STAGE) { 
			return croppedImg; // when we only want to see the cropped image
		}
		
		Mat vessel = VesselContour.findVesselContour(croppedImg, thresholdVesselContour, kernelVesselContour);
		if (stage == Window.MASKING_STAGE) {
			return vessel; //when we want to visualize the mask
		}
		
		return EllipseFinder.getEllipse(croppedImg, vessel);
	}
}
