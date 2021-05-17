package cv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Static class that contains methods to extract contours and work with them
 * 
 * @author yann
 * @version 1.0
 */
public class ContourUtils {

	/**
	 * Extracts the vertical and horizontal contours of a Mat object and returns it
	 * as a Mat object
	 * 
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

	// https://docs.opencv.org/3.4/d2/d2c/tutorial_sobel_derivatives.html
	public static Mat sobelEdge(Mat src, int ddepth, int ksize, double scale, double delta) {
		Mat grad = new Mat();
		Mat grad_x = new Mat(), grad_y = new Mat();
		Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();

		Imgproc.Sobel(src, grad_x, ddepth, 1, 0, ksize, scale, delta, Core.BORDER_DEFAULT);
		Imgproc.Sobel(src, grad_y, ddepth, 0, 1, ksize, scale, delta, Core.BORDER_DEFAULT);

		Core.convertScaleAbs(grad_x, abs_grad_x);
		Core.convertScaleAbs(grad_y, abs_grad_y);
		Core.addWeighted(abs_grad_x, 0.2, abs_grad_y, 0.5, 0, grad);

		return grad;
	}

	/**
	 * Returns the Canny-Sobel edge of an image.
	 * 
	 * @param source    the source image
	 * @param threshold the threshold value used for Canny and Sobel edges
	 * @param kernel the kernel size
	 * @return the binary image of Canny-Sobel edge
	 * @author Cyril Dubos
	 */
	public static Mat cannySobelEdge(Mat source, int threshold, int kernel) {
		// Canny edge
		Mat cannyEdge = new Mat();
		Imgproc.GaussianBlur(source, cannyEdge, new Size(7, 7), 1.1);
		Imgproc.Canny(source, cannyEdge, threshold / 3, threshold);

		// Sobel edge
		Mat sobelEdge = new Mat();
		Imgproc.GaussianBlur(source, sobelEdge, new Size(7, 7), 1.1);
		sobelEdge = ContourUtils.sobelEdge(sobelEdge, CvType.CV_16S, 3, 1, 1);
		Imgproc.threshold(sobelEdge, sobelEdge, threshold / 2, 255, Imgproc.THRESH_BINARY); // todo: slider

		// Merge Canny and Sobel edges
		Mat destination = new Mat();

		Core.add(cannyEdge, sobelEdge, destination);

		destination = Segmentation.simpleBinarization(destination, 1, false);
		destination = Morphology.closing(destination, kernel); // todo: kernel size

		return destination;
	}
}
