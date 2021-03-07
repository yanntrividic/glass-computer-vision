package cv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

/**
 * Static class with method to perform segmentation on images.
 * @author Yann Trividic
 * @version 1.0
 */
public class Segmentation {
	public static Mat binaryThreshold(Mat gray, int threshold, boolean show) {
		if(gray.type() != CvType.CV_8UC1) throw new IllegalArgumentException("The image must be CV_8UC1") ;
		Mat binary = new Mat() ;
		Imgproc.threshold(gray, binary, threshold, 255, Imgproc.THRESH_BINARY); //binarization
		if(show) {
			HighGui.imshow("threshold="+threshold, binary); // displays test.png
			HighGui.waitKey(); // waits before executing the rest	
		}
		return binary; 
	}
}
