package cv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Static class that contains methods to extract contours and work with them
 * @author yann
 * @version 1.0
 */
public class ContourUtils {
	
	/**
	 * Extracts the vertical and horizontal contours of a Mat object and returns it as a Mat object
	 * @param src the source Mat object
	 * @return dst a Mat object that contains the contour of the image
	 * @author yann
	 */
	public static Mat sobelFilter(Mat src) {
		// Creating an empty matrix for the destination image
		Mat horizontalContour = new Mat();
		Mat verticalContour = new Mat();
		Mat dst = new Mat();

		// Applying sobel derivative with values x:0 y:1
		Imgproc.Sobel(src, horizontalContour, -1, 0, 1);

		// Applying sobel derivative with values x:1 y:0
		Imgproc.Sobel(src, verticalContour, -1, 1, 0);

		Core.add(horizontalContour, verticalContour, dst);
		return dst;
	}
}
