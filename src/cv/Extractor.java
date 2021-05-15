package cv;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import io.JSONLabelProcessing;
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
			int kernelVesselContour, int resizeWidth, int angle) {

		// System.out.println("medianFilterKSize="+medianFilterKSize+"\n"+
		// "alphaSrc="+alphaSrc+"\n"+
		// "alphaMask="+alphaMask+"\n"+
		// "intensityThreshold="+intensityThreshold+"\n"+
		// "contourThreshold="+contourThreshold+"\n"+
		// "minimumSurface="+minimumSurface+"\n") ;
		
		Mat mask = Imgcodecs.imread(Reader.getResourcesDir() + "gaussian_distribution.jpg");
		mask = PreProcessing.rgbToGrayScale(mask);

		Mat resizedMat = PreProcessing.resizeSpecifiedWidth(mat, 500);
		Mat grayScale = PreProcessing.rgbToGrayScale(resizedMat);
		grayScale = PreProcessing.medianFilter(grayScale, medianFilterKSize);

		Mat resizedMatWithMask = ProcessingUtils.applyMask(grayScale, alphaSrc, mask, alphaMask, 0.0);

		Point[] rectCorners = SpecularReflexion.findSpecularReflexion(resizedMatWithMask, intensityThreshold, contourThreshold);

		Mat croppedImg = ProcessingUtils.getCroppedImageFromTopLeftBotRight(grayScale, rectCorners[0], rectCorners[1], minimumSurface);
		
		if(stage == Window.CROPPING_STAGE) { 
			return ProcessingUtils.drawRectFromCorners(resizedMat, rectCorners); // when we only want to see the cropped image
		}
		
		Mat vessel = VesselContour.findVesselContour(croppedImg, thresholdVesselContour, kernelVesselContour);
		
		if (stage == Window.MASKING_STAGE) {
			return drawContourMaskOnOriginalImage(resizedMat, rectCorners[0], vessel) ;
		}
		
		ArrayList<Double> ell = EllipseFinder.getEllipse(croppedImg, vessel, resizeWidth, angle);
		
		Point leftEllipse = new Point(ell.get(0), ell.get(1));
		Point rightEllipse = new Point(ell.get(2), ell.get(3));
		double ellHeight = ell.get(4);
		
		Point middleEllipse = new Point((leftEllipse.x + rightEllipse.x)/2, (leftEllipse.y + rightEllipse.y)/2);
		Point bottomEllipse = new Point(middleEllipse.x, middleEllipse.y + ellHeight/2);
		
		//Get the two points of the glass (top and bottom)
		//Mat line = JSONLabelProcessing.drawLineAngle(middleEllipse, angle, false, 
		//		(int)croppedImg.size().height, (int)croppedImg.size().width).mul(croppedImg);
		
		int[] boundaries;
		try {
			boundaries = ProcessingUtils.getMaskBoundaries(vessel);
			//Ratio between dist(bottomEllipse, bottomGlass) and dist(topGlass, bottomGlass)
			double glass = Math.abs(boundaries[0] - boundaries[2]);
			glass = (glass==0)? 1:glass;
			double fillingLevel = JSONLabelProcessing.euclideanDistance(bottomEllipse, 
					new Point(bottomEllipse.x, boundaries[2]));
			
			//TODO : add the path to the corresponding JSON file
			double fillingLevelJSON = JSONLabelProcessing.liquidLevel("pathToJson");
			
			System.out.println("Filling level found : " + (fillingLevel/glass)*100 );
			System.out.println("Filling level via JSON file : " + fillingLevelJSON);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
				
		double startingX = rectCorners[0].x ;
		double startingY = rectCorners[0].y ;
		System.out.println("x1 "+ell.get(0)+" y1 "+ell.get(1)+" x2 "+ell.get(2)+" x3 "+ell.get(3)+" h "+ell.get(4));
		return EllipseFinder.drawEllipse(new Point(ell.get(0) + startingX, ell.get(1) + startingY),
				 new Point(ell.get(2) + startingX, ell.get(3) + startingY),
				 drawContourMaskOnOriginalImage(ProcessingUtils.drawRectFromCorners(resizedMat, rectCorners), 
						 						rectCorners[0], vessel), ell.get(4));
		
	}
	/*new Point(ell.get(0) + startingX, ell.get(1) + startingY),
	 new Point(ell.get(2) + startingX, ell.get(3) + startingY),
	 drawContourMaskOnOriginalImage(ProcessingUtils.drawRectFromCorners(resizedMat, rectCorners), 
			 						rectCorners[0], vessel), ell.get(4));*/
	private static Mat drawContourMaskOnOriginalImage(Mat resizedMat, Point topLeftCorner, Mat mask) {
		Mat maskOriginalDimsBinary = ProcessingUtils.putMaskInMatOriginalSize(resizedMat.size(), topLeftCorner, mask); //when we want to visualize the mask
		
		Mat maskContours = new Mat();
		Imgproc.Laplacian(maskOriginalDimsBinary, maskContours, maskContours.type(), 3, 1, 0, Core.BORDER_DEFAULT );
		//Mat maskContours = ContourUtils.sobelFilter(maskOriginalDimsBinary) ;
		
		Mat maskOriginalDimsRGB = new Mat() ;
		Imgproc.cvtColor(maskContours, maskOriginalDimsRGB, Imgproc.COLOR_GRAY2RGB);
		return ProcessingUtils.applyMask(resizedMat, 1, maskOriginalDimsRGB, 0.5, 0.0) ;		
	}
}
