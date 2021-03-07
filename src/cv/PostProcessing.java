package cv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Static class with methods to postprocess images.
 * @author Yann Trividic
 * @version 1.0
 */
public class PostProcessing {
	
	public static Mat opening(Mat src, int size) {
		Mat dst = new Mat() ;
		Mat kernel = Mat.ones(size, size, CvType.CV_32F);
		Imgproc.morphologyEx(src, dst, Imgproc.MORPH_OPEN, kernel);
		return dst ;
	}
	
	public static Mat closing(Mat src, int size) {
		Mat dst = new Mat() ;
		Mat kernel = Mat.ones(size,size, CvType.CV_32F);
		Imgproc.morphologyEx(src, dst, Imgproc.MORPH_CLOSE, kernel);
		return dst ;
	}
}
