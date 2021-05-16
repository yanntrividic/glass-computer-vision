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

	/**
	 * Main method of the glass processing algorithm. Depending on value of stage, can return various Mat objects that
	 * correspond to certain stages of the processing.
	 * 
	 * @param stage two values possible : 
	 * Window.CROPPING_STAGE for when we want to stop the processing after the cropping stage
	 * Window.MASKING_STAGE for stopping after extracting the mask contours
	 * Every other value will result in the full processing of the image.
	 * @param mat The original Mat object. Typically, it's a RGB image that contains a glass with water in it.
	 * @param win Window object (controller of the program) that holds the labels info for computing the final evalutation
	 * @param medianFilterKSize Parameter relative to the CROPPING_STAGE (see SpecularReflexion)
	 * @param alphaSrc Parameter relative to the CROPPING_STAGE (see SpecularReflexion)
	 * @param alphaMask Parameter relative to the CROPPING_STAGE (see SpecularReflexion)
	 * @param intensityThreshold Parameter relative to the CROPPING_STAGE (see SpecularReflexion)
	 * @param contourThreshold Parameter relative to the CROPPING_STAGE (see SpecularReflexion)
	 * @param minimumSurface Parameter relative to the MASKING_STAGE (see VesselContour)
	 * @param thresholdVesselContour Parameter relative to the MASKING_STAGE (see VesselContour)
	 * @param kernelVesselContour Parameter relative to the MASKING_STAGE (see VesselContour)
	 * @param resizeWidth Parameter relative to finding the ellipse (see EllipseFinder)
	 * @param angle Parameter relative to finding the ellipse (see EllipseFinder)
	 * 
	 * @return Depending on the value of the stage parameter, the method will return the original Mat with various
	 * indicators of the processing's result : a blue rectangle for the area where it's most probable there is a glass,
	 * a white outline for the glass contours, and a red ellipse for where the water level is believed to be.
	 * 
	 * @see Window
	 * @see SpecularReflexion
	 * @see VesselContour
	 * @see EllipseFinder
	 */
	public static Mat computeImage(int stage, Mat mat, Window win,
			int medianFilterKSize, double alphaSrc, double alphaMask, double intensityThreshold, 
			double contourThreshold, double minimumSurface, 
			int thresholdVesselContour,	int kernelVesselContour, 
			int resizeWidth, int angle) {

		// System.out.println("medianFilterKSize="+medianFilterKSize+"\n"+
		// "alphaSrc="+alphaSrc+"\n"+
		// "alphaMask="+alphaMask+"\n"+
		// "intensityThreshold="+intensityThreshold+"\n"+
		// "contourThreshold="+contourThreshold+"\n"+
		// "minimumSurface="+minimumSurface+"\n") ;
		
		Mat mask = Imgcodecs.imread(Reader.getGaussianMaskPath());
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
			Mat contourMaskOnOriginalImage = drawContourMaskOnOriginalImage(resizedMat, rectCorners[0], vessel) ;
			return ProcessingUtils.drawRectFromCorners(contourMaskOnOriginalImage, rectCorners);
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
			System.out.println(win.getCurrentImageLabelPath()) ;
			double fillingPercentageJSON = JSONLabelProcessing.liquidLevel(win.getCurrentImageLabelPath());
			double fillingPercentage = (fillingLevel/glass)*100;
			double errorPercentage = (Math.abs(fillingPercentage - fillingPercentageJSON)
										/fillingPercentageJSON)*100;
			
			//TODO : @yann add those numbers to the view
			System.out.println("Filling percentage found : " +  fillingPercentage);
			System.out.println("Filling percentage via JSON file : " + fillingPercentageJSON);
			System.out.println("Error percentage : " + errorPercentage);
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
