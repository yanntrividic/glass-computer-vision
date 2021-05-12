package cv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Static class with method to perform segmentation on images.
 * @author Yann Trividic
 * @version 1.0
 */
public class Segmentation {
	
	/**
	 * A simple binarization tool for GrayScale Mat objects
	 * @param gray A GrayScale Mat object (CV_8UC1)
	 * @param threshold int value between 0 and 255 to binarize at this threshold
	 * @param show if show is true, the image is displayed
	 * @return a new Mat object, a binarized version of the input Mat object
	 * @author Yann Trividic
	 */
	public static Mat simpleBinarization(Mat gray, int threshold, boolean show) {
		if(gray.type() != CvType.CV_8UC1) throw new IllegalArgumentException("The image must be CV_8UC1") ;
		Mat binary = new Mat() ;
		Imgproc.threshold(gray, binary, threshold, 255, Imgproc.THRESH_BINARY); //binarization
		
		if(show) ui.Utils.displayImage(binary, "threshold="+threshold);
		
		return binary; 
	}
}
