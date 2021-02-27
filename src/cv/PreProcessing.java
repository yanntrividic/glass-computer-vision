package cv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class PreProcessing {
	
	/**
	 * Creates a GrayScale Mat object out of a RGB Mat object using OpenCV
	 * @param rgb A Mat object that contains the image to process
	 * @return a new Mat object with the CV_8UC1 type
	 */
	public static Mat rgbToGrayScale(Mat rgb) {
		Mat gs = new Mat();
		Imgproc.cvtColor(rgb, gs, Imgproc.COLOR_RGB2GRAY); // to grayscale
		return gs ;
	}
	
	public static Mat equalizeGrayMat(Mat gray) {
		if(gray.type() != CvType.CV_8UC1) throw new IllegalArgumentException("The image must be CV_8UC1") ;
		Mat equalized = new Mat();
		// TODO: continue this method with Imgproc.equalizeHist()
		return equalized ;
	}
}
