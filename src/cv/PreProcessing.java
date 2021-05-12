package cv;

import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


/**
 * Static class with method to preprocess images.
 * @author Yann Trividic
 * @version 1.0
 */
public class PreProcessing {
	
	/**
	 * Creates a GrayScale Mat object out of a RGB Mat object using OpenCV
	 * @param rgb A Mat object that contains the image to process
	 * @return a new Mat object with the CV_8UC1 type
	 * @author Yann Trividic
	 */
	public static Mat rgbToGrayScale(Mat rgb) {
		Mat gs = new Mat();
		try {
			Imgproc.cvtColor(rgb, gs, Imgproc.COLOR_RGB2GRAY); // to grayscale
		} catch(CvException e) {
			System.err.println("The image path is not valid.") ;
		}
		return gs ;
	}
	
	/**
	 * Creates an equalized Mat object out of a GrayScale Mat object
	 * @param gray Mat objet to equalize, has to be CV_8UC1
	 * @return equalized image in a Mat object
	 * @author Yann Trividic
	 */
	public static Mat equalizeGrayMat(Mat gray) {
		if(gray.type() != CvType.CV_8UC1) throw new IllegalArgumentException("The image must be CV_8UC1") ;
		Mat equalized = new Mat();
		Imgproc.equalizeHist(gray, equalized);
		return equalized ;
	}
	
	/**
	 * Computes a blurred image from a source image using the median filter
	 * @param src a source image contained in a Mat object
	 * @param kernelSize the size of the side of the kernel used for the operation
	 * @return a blurred image in a Mat object
	 * @author Yann Trividic
	 */
	public static Mat medianFilter(Mat src, int kernelSize) {
		Mat dst = new Mat() ;
		Imgproc.medianBlur(src, dst, kernelSize);
		return dst ;
	}

	/**
	 * Computes a blurred image from a source image using the mean filter
	 * @param src a source image contained in a Mat object
	 * @param kernelSize the size of the side of the kernel used for the operation
	 * @return a blurred image in a Mat object
	 * @author Yann Trividic
	 */
	public static Mat meanFilter(Mat src, int kernelSize) {
		Mat dst = new Mat() ;
		Imgproc.blur(src, dst, new Size(kernelSize, kernelSize));
		return dst ;
	}
	
	/**
	 * Resizes a Mat object to a specified width and preserves the original ratio
	 * @param mat the Mat object to resize
	 * @param wantedWidth int, the wanted width
	 * @return a new Mat object of the specified width with the same ratio as the original image
	 * @author Yann Trividic
	 */
	public static Mat resizeSpecifiedWidth(Mat mat, int wantedWidth) {
		int displayHeight = (int) ((double) mat.height() /mat.width()  * wantedWidth) ;
		Mat resized = new Mat() ;
		Imgproc.resize(mat, resized, new Size(wantedWidth, displayHeight));
		return resized ;
	}
}
