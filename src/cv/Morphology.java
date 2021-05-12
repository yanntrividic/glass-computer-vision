package cv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Static class with methods to postprocess images.
 * @author Yann Trividic
 * @version 1.0
 */
public class Morphology {
	
	/**
	 * Returns the opened image (according to the morphological opening) by using a sizexsize full kernel
	 * @param src Mat object that has the image to open, has to be a GreyScale or binary image
	 * @param size int size of the side of the kernel used for the operation
	 * @return a opened GrayScale or binary image in a Mat object
	 * @author yann
	 */
	public static Mat opening(Mat src, int size) {
		Mat dst = new Mat() ;
		Mat kernel = Mat.ones(size, size, CvType.CV_32F);
		Imgproc.morphologyEx(src, dst, Imgproc.MORPH_OPEN, kernel);
		return dst ;
	}

	/**
	 * Returns the closed image (according to the morphological closing) by using a sizexsize full kernel
	 * @param src Mat object that has the image to close, has to be a GreyScale or binary image
	 * @param size int size of the side of the kernel used for the operation
	 * @return a closed GrayScale or binary image in a Mat object
	 * @author yann
	 */
	public static Mat closing(Mat src, int size) {
		Mat dst = new Mat() ;
		Mat kernel = Mat.ones(size,size, CvType.CV_32F);
		Imgproc.morphologyEx(src, dst, Imgproc.MORPH_CLOSE, kernel);
		return dst ;
	}
}
