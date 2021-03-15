package cv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Static class with utilitary methods to work on images.
 * @author Yann Trividic
 * @version 1.0
 */
public class Utils {
	
	/**
	 * If a Point object isn't within the boundaries of the Mat object, the returned Point object has coordinates
	 * within the ranges of the Mat.
	 * @param mat The reference image
	 * @param p The point to clone and modify
	 * @return A new Point object within the boundaries of the image
	 */
	public static Point getPointWithinBoundaries(Mat mat, Point p) {
		Point vp = p.clone() ;
		int width = mat.width() ;
		int height = mat.height() ;
		
		if(p.x > width) vp.x = width ;
		if(p.x < 0) vp.x = 0 ;
		if(p.y > height) vp.y = height ;
		if(p.y < 0) vp.y = 0 ;
		
		return vp ;		
	}
	
	public static byte[] getMatAsByteArray(Mat m) {
		byte[] temp = new byte[(int) m.total()]; // declaration of a byte array the size of the Mat
		m.get(0, 0, temp) ; // the content of the image is put inside the temp variable
		return temp ;
	}	
	
	// FIXME: I don't understand why the output is always {0, 255}. Maybe it has to do with the byte type ?
	public static int[] getMinMaxGrayScaleImg(Mat mat) {
		byte[] img = getMatAsByteArray(mat) ;
		for(int i = 0 ; i < 20 ; i++) System.out.print(img[i]+128+", ") ;
		
		int min = Integer.MAX_VALUE ;
		int max = Integer.MIN_VALUE ;
		for(int i = 0 ; i < img.length ; i++) {
			int castedValue = 0x80 + (int) img[i]; // to [0..255] range
			if((int) castedValue < min) min = castedValue ;
			if((int) castedValue > max) max = castedValue ;
		}
		return new int [] {min, max} ;
	}
	
	//testwithMinMaxLoc
	public static int[] getMinMaxGrayScaleImgInexpensive(Mat mat) {
		System.out.println(mat.width());
		//Mat mask = Mat.ones(mat.size(), CvType.CV_8U); 
		double max = Core.minMaxLoc(mat).maxVal;
		double min = Core.minMaxLoc(mat).minVal ;
		return new int [] {(int) min, (int) max} ;
	}
	
	// TODO: This method is the same as above, it's just a lot heavier to process, but it works
	public static int[] getMinMaxGrayScaleImgVeryExpensive(Mat mat) {		
		double min = Integer.MAX_VALUE ;
		double max = Integer.MIN_VALUE ;
		
		for(int x = 0 ; x < mat.width() ; x++) {
			for(int y = 0 ; y < mat.height() ; y++) {
				double [] px = mat.get(y, x) ;
				if(px[0] > max) max = px[0] ;
				if(px[0] < min) min = px[0] ;
			}
		}
		return new int [] {(int) min, (int) max} ;
	}
	
	/**
	 * Applies a Mast mask on a source image
	 * @param src Mat on which apply the mask
	 * @param transparencySrc double Transparency of the src image
	 * @param mask Mat which corresponds to the masl
	 * @param transparencyMask double Transparency of the mask image
	 * @param gamma double gamma factor
	 * @return a new Mat object with the applied mask on it
	 */
	public static Mat applyMask(Mat src, double transparencySrc, Mat mask, double transparencyMask, double gamma) {
		Mat dst = new Mat() ;
		Mat resizedMask = new Mat() ;

		// The mask is resized to fit the size of the src image
		Imgproc.resize(mask, resizedMask, new Size(src.width(), src.height()));
		
		// TODO: Tweak those parameters to see what gives the best results
		Core.addWeighted(src, transparencySrc, resizedMask, transparencyMask, gamma, dst);
		return dst;
	}
	
	//TODO: There's plenty of room to optimize this
	public static int[] getMaskBoundaries(Mat mask) {
		int top = Integer.MAX_VALUE ;
		int bot = Integer.MIN_VALUE ;
		int left = Integer.MAX_VALUE ;
		int right = Integer.MIN_VALUE ;
		
		for(int x = 0 ; x < mask.width() ; x++) {
			for(int y = 0 ; y < mask.height() ; y++) {
				if(mask.get(y, x)[0] != 0) {
					if(top > y) top = y ;
					if(bot < y) bot = y ;
					if(left > x) left = x ;
					if(right < x) right = x ;
				}
			}
		}
		//System.out.println(top +" "+right+" "+bot+" "+left) ;
		return new int[] {top, right, bot, left} ;
	}
	
	public static Mat drawRectFromBoudaries(Mat src, int[] boundaries, int[] offset) {
		int top = (boundaries[0] - offset[0] < 0)?0:(boundaries[0] - offset[0]) ;
		int right = (boundaries[1] + offset[1] > src.width()-1)?(src.width()-1):(boundaries[1] + offset[1]) ;
		int bot = (boundaries[2] + offset[2] > src.height()-1)?(src.height()-1):(boundaries[2] + offset[2]) ;
		int left = (boundaries[3] - offset[3] < 0)?0:(boundaries[3] - offset[3]) ;
				
		Point topLeft = new Point(left, top) ;
		Point botRight = new Point(right, bot) ;
		Imgproc.rectangle(src, topLeft, botRight, new Scalar(255,255,255), 2);
		return src ;
	}
}
