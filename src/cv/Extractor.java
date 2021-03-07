package cv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
//import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

/**
 * Static class with methods to extract characteristics from images.
 * @author Yann Trividic
 * @version 1.0
 */
public class Extractor {
	public static Mat sobelFilter(Mat src) {
		//Creating an empty matrix for the destination image
		Mat horizontalContour = new Mat() ;
		Mat verticalContour = new Mat() ;
		Mat dst = new Mat();	
		
		//Applying sobel derivative with values x:0 y:1
		Imgproc.Sobel(src, horizontalContour, -1, 0, 1);
		//HighGui.imshow("Sobel - x:0 & y:1 ", horizontalContour);
		//HighGui.waitKey();
		
		//Applying sobel derivative with values x:1 y:0
		Imgproc.Sobel(src, verticalContour, -1, 1, 0);
		//HighGui.imshow("Sobel - x:1 & y:0 ", verticalContour);
		//HighGui.waitKey();
		
		Core.add(horizontalContour, verticalContour, dst);
		return dst ;
		
	}
}
