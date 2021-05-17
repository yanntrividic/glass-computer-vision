package cv;

import java.util.ArrayList;

import org.opencv.core.Core;
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

		/* CROPPING STAGE */
		// The mask used for cropping the image is read from its file
		Mat mask = Imgcodecs.imread(Reader.getGaussianMaskPath());
		mask = PreProcessing.rgbToGrayScale(mask);

		// We then resize the mat to make the dimensions consistent over the dataset and speed up the process
		Mat resizedMat = PreProcessing.resizeSpecifiedWidth(mat, 500);
		Mat grayScale = PreProcessing.rgbToGrayScale(resizedMat);
		grayScale = PreProcessing.medianFilter(grayScale, medianFilterKSize);

		// The mask is applied to the image to process, it supposes that the glass is approximately in the center of the img
		Mat resizedMatWithMask = ProcessingUtils.applyMask(grayScale, alphaSrc, mask, alphaMask, 0.0);

		// We extract the area of interest, i.e. the one where a glass is believed to be in the image
		Point[] rectCorners = SpecularReflexion.findSpecularReflexion(resizedMatWithMask, intensityThreshold, contourThreshold);

		// The image is cropped to fit the area of interest found
		Mat croppedImg = ProcessingUtils.getCroppedImageFromTopLeftBotRight(grayScale, rectCorners[0], rectCorners[1], minimumSurface);
		
		if(stage == Window.CROPPING_STAGE) { 
			return ProcessingUtils.drawRectFromCorners(resizedMat, rectCorners); // when we only want to see the cropped image
		}

		/* MASKING STAGE */
		// We get the vessel
		Mat vessel = VesselContour.findVesselContour(croppedImg, thresholdVesselContour, kernelVesselContour);
		// And then uncrop the mask to make it fit on the original image
		Mat uncroppedVessel = ProcessingUtils.putMaskInMatOriginalSize(resizedMat.size(), rectCorners[0], vessel);
		
		// If stage is MASKING_STAGE, then we exit the method here
		if (stage == Window.MASKING_STAGE) {
			Mat contourMaskOnOriginalImage = drawContourMaskOnOriginalImage(resizedMat, uncroppedVessel) ;
			return ProcessingUtils.drawRectFromCorners(contourMaskOnOriginalImage, rectCorners);
		}
		
		/* FINAL STAGE */
		// Otherwise, we compute the ellipse out of the croppedImg and the cropped vessel
		ArrayList<Double> ell = EllipseFinder.getEllipse(croppedImg, vessel, resizeWidth, angle);
		// And then uncrop the ellipse to make it fit on the original image
		double [] uncroppedEll = uncropEllipse(ell, rectCorners[0]) ;
		
		// At this point, we can evaluate our result. It will be displayed both in the terminal and in the GUI
		evaluate(win, uncroppedVessel, uncroppedEll, ell);
		
		// We can then return the final image
		return EllipseFinder.drawEllipse(
				new Point(uncroppedEll[0], uncroppedEll[1]),
				new Point(uncroppedEll[2], uncroppedEll[3]),
				drawContourMaskOnOriginalImage(ProcessingUtils.drawRectFromCorners(resizedMat, rectCorners), uncroppedVessel),
				ell.get(4));
	}

	private static double[] uncropEllipse(ArrayList<Double> ell, Point topLeft) {
		return new double[] { ell.get(0) + topLeft.x, 
							  ell.get(1) + topLeft.y,
							  ell.get(2) + topLeft.x,
							  ell.get(3) + topLeft.y };
	}
	
	private static Mat drawContourMaskOnOriginalImage(Mat resizedMat, Mat maskOriginalDimsBinary) {
		Mat maskContours = new Mat();
		Imgproc.Laplacian(maskOriginalDimsBinary, maskContours, maskContours.type(), 3, 1, 0, Core.BORDER_DEFAULT );
		//Mat maskContours = ContourUtils.sobelFilter(maskOriginalDimsBinary) ;
		
		Mat maskOriginalDimsRGB = new Mat() ;
		Imgproc.cvtColor(maskContours, maskOriginalDimsRGB, Imgproc.COLOR_GRAY2RGB);
		return ProcessingUtils.applyMask(resizedMat, 1, maskOriginalDimsRGB, 0.5, 0.0) ;		
	}
	
	/**
	 * @param win
	 * @param vessel
	 * @param ell
	 * @param ellipseHeight
	 * @return
	 */
	private static void evaluate(Window win, Mat vessel, double [] ell, ArrayList<Double> resEll) {
		/* PREPROCESSING WHAT WE NEED TO EVALUATE */
		Point leftEllipse = new Point(ell[0], ell[1]);
		Point rightEllipse = new Point(ell[2], ell[3]);
		
		Point middleEllipse = new Point((leftEllipse.x + rightEllipse.x)/2, (leftEllipse.y + rightEllipse.y)/2);
		Point bottomEllipse = new Point(middleEllipse.x, middleEllipse.y + resEll.get(4)/2);
		
		//here is the Mat for the ellipse. It's the same dims as the vessel's image.
		System.out.println("\n"+leftEllipse+ " "+ " "+rightEllipse + " "+resEll.get(4)) ;
		
		Mat ellipseMat = EllipseFinder.drawFilledEllipse(leftEllipse, rightEllipse, Mat.zeros(vessel.size(), 0), resEll.get(4));
		//the ratio is the same as the original image. We can resize the label using the size of one of these Mat
		
		int [] boundaries = null ;
	
		try {
			boundaries = ProcessingUtils.getMaskBoundaries(vessel);
		} catch (Exception e) {
			System.out.println(e.getMessage()); // when the mat is all black (no vessel found)
			return;
		}
		
		// We can also format the text for the ellipse confidence criterion
		String resEllS = "Ellipse confidence: "+String.format("%,.2f",resEll.get(5))+"%";
		
		/* FILLING PERCENTAGE COMPARISON */
		//Ratio between dist(bottomEllipse, bottomGlass) and dist(topGlass, bottomGlass)
		double glass = Math.abs(boundaries[0] - boundaries[2]);
		glass = (glass==0)? 1:glass;
		double fillingLevel = JSONLabelProcessing.euclideanDistance(bottomEllipse, 
				new Point(bottomEllipse.x, boundaries[2]));
		
		System.out.println(win.getCurrentImageLabelPath()) ;
		double fillingPercentageJSON = JSONLabelProcessing.liquidLevel(win.getCurrentImageLabelPath());
		double fillingPercentage = (fillingLevel/glass)*100;
		double errorPercentage = (Math.abs(fillingPercentage - fillingPercentageJSON)
									/fillingPercentageJSON)*100;
		
		//if we've correclty found an empty glass
		if(fillingPercentageJSON == 0 && fillingPercentage == 0)
			errorPercentage = 0;
		
		// text for the filling percentage evaluation
		String filPer = "Filling % found: " +  String.format("%,.2f", fillingPercentage) +"%";
		String filPerLabel = "Filling % via JSON file: " + String.format("%,.2f", fillingPercentageJSON) +"%";
		String errPer = "Filling % error: " + String.format("%,.2f", errorPercentage) +"%";
		
		Mat[] filledLabels = io.Reader.getFilledLabels(win.getCurrentImageLabelPath());
		Mat ellipseLabel = filledLabels[0];
		Mat glassLabel = filledLabels[1];
		ellipseLabel = PreProcessing.resizeSpecifiedWidth(ellipseLabel, (int) vessel.size().width);
		glassLabel = PreProcessing.resizeSpecifiedWidth(glassLabel, (int) vessel.size().width);
		
		/* COMPUTING INTERSECTION OVER UNION FOR THE GLASS AND THE ELLIPSE RELATIVE TO THE LABELS */
		//IoU for the glass
		Mat intersectionGlass = glassLabel.mul(vessel);
		Mat unionGlass = new Mat();
		Core.add(glassLabel, vessel, unionGlass);
		
		//IoU for the ellipse
		Mat intersectionEllipse =  ellipseLabel.mul(ellipseMat);
		Mat unionEllipse = new Mat();
		Core.add(ellipseLabel, ellipseMat, unionEllipse);
		
		//Compute the final IoU values
		double iouGlassValue = (double) Core.countNonZero(intersectionGlass)/Core.countNonZero(unionGlass)*100;
		double iouEllipseValue = (double) Core.countNonZero(intersectionEllipse)/Core.countNonZero(unionEllipse)*100;
		
		// text for the IoU 
		String IoUGlass = "Glass IoU: " + String.format("%,.2f", iouGlassValue)+"%";
		String IoUEllipse = "Ellipse IoU: " + String.format("%,.2f", iouEllipseValue)+"%";
		
		/* MEAN ERROR */
		double meanError = (errorPercentage + (100 - iouGlassValue) + (100 - iouEllipseValue))/3 ;
		String meanErrorS = "Mean error: " + String.format("%,.2f", meanError)+"%";
		
		
		/* RESULT DISPLAY */
		System.out.println(filPer+"\n"+filPerLabel+"\n"+errPer+"\n"+resEllS+"\n"+IoUGlass+"\n"+IoUEllipse+"\n"+meanErrorS);
		//win.updateText("Computed " + win.getImgs().get(win.getImgIndex()) + 
		//		": "+ errPer+", " + IoUGlass + ", " + IoUEllipse + ", " + meanErrorS);
		win.updateText(new String [] {"Computed " + win.getImgs().get(win.getImgIndex()) +
				":", "", filPer, errPer, "", resEllS, "", IoUGlass, IoUEllipse, "", meanErrorS });
		
	}
}
