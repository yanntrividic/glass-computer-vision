package cv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Static class with utilitary methods to work on images.
 * @author Yann Trividic
 * @version 1.0
 */
public class ProcessingUtils {
	
	/**
	 * If a Point object isn't within the boundaries of the Mat object, the returned Point object has coordinates
	 * within the ranges of the Mat.
	 * @param mat The reference image
	 * @param p The point to clone and modify
	 * @return A new Point object within the boundaries of the image
	 * @author Yann Trividic
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
	
	/**
	 * Returns a byte array from the original Mat object
	 * @param m Mat to make into a byte array
	 * @return the byte array that contains the Mat object data
	 * @author Yann Trividic
	 */
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
	 * @author Yann Trividic
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
	
	/**
	 * Method to get the boundaries of the rectangle in which the mask is enclosed
	 * @param mask Mat object from which we want to get the boundaries
	 * @return a list of four int coordinates : {top, right, bot, left}
	 * @throws Exception if the Mat object doesn't have any non-zero values
	 * @author Yann Trividic
	 */
	public static int[] getMaskBoundaries(Mat mask) throws Exception {
		if(Core.countNonZero(mask) == 0) throw new Exception("The mask is all black") ;
		
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
		return new int[] {top, right, bot, left} ;
	}

	/**
	 * Gets the top left and bottom right points of a rectangle from its boundaries
	 * @param src the source image
	 * @param boundaries the boundaries as described in getMaskBoundaries(Mat)
	 * @param offset adds an offset on the sides
	 * @return a list of two Point objects : {topLeft, botRight}
	 * @see getMaskBoundaries(Mat)
	 * @author Yann Trividic
	 */
	public static Point[] getTwoCornersPlusBoundaries(Mat src, int[] boundaries, int[] offset) {
		int top = (boundaries[0] - offset[0] < 0)?0:(boundaries[0] - offset[0]) ;
		int right = (boundaries[1] + offset[1] > src.width()-1)?(src.width()-1):(boundaries[1] + offset[1]) ;
		int bot = (boundaries[2] + offset[2] > src.height()-1)?(src.height()-1):(boundaries[2] + offset[2]) ;
		int left = (boundaries[3] - offset[3] < 0)?0:(boundaries[3] - offset[3]) ;
		
		Point topLeft = new Point(left, top) ;
		Point botRight = new Point(right, bot) ;
		
		return new Point [] {topLeft, botRight};
	}
	
	/**
	 * Draws a rectangle from its boundaries and potentially adds an offset to it
	 * @param src the source image on which to draw the rectangle (Mat object
	 * @param boundaries the boundaries as described in getMaskBoundaries(Mat)
	 * @param offset adds an offset on the sides
	 * @return the src Mat object with a white rectangle drawn on it
	 * @see getMaskBoundaries(Mat)
	 * @author Yann Trividic
	 */
	public static Mat drawRectFromBoudaries(Mat src, int[] boundaries, int[] offset) {
		Point [] points = getTwoCornersPlusBoundaries(src, boundaries, offset) ;
		
		Imgproc.rectangle(src, points[0], points[1], new Scalar(255,255,255), 2);
		return src ;
	}
	
	/**
	 * Draws a rectangle from its corners
	 * @param src the source image on which to draw the rectangle (Mat object
	 * @param corners topleft and bottomright corners of the rectangle
	 * @return the src Mat object with a white rectangle drawn on it
	 * @see getMaskBoundaries(Mat)
	 * @author Yann Trividic
	 */
	public static Mat drawRectFromCorners(Mat src, Point[] corners) {
		Imgproc.rectangle(src, corners[0], corners[1], new Scalar(255, 0, 0), 2);
		return src ;
	}
	
	/**
	 * Crops a Mat object according to a top left and bottom right points 
	 * @param src the Mat object to crop
	 * @param topLeft Point with valid coordinates for src
	 * @param botRight Point with valid coordinates for src
	 * @param minimumSurface a lower threshold for when the surface to crop seems to small to hold a glass
	 * @return a cropped image of the original Mat object
	 * @author Yann Trividic
	 */
	public static Mat getCroppedImageFromTopLeftBotRight(Mat src, Point topLeft, Point botRight, double minimumSurface) {
		Rect rectCrop = new Rect(topLeft, botRight) ;
		double filledPercetage = (botRight.x-topLeft.x)*(botRight.y-topLeft.y)/(src.width()*src.height()) ;
		
		if(filledPercetage < minimumSurface) {
			return new Mat(src, rectCrop) ;
		}
		
		return src;
	}
	

	public static Mat putMaskInMatOriginalSize(Size size, Point topLeft, Mat mask) {
		Mat dst = Mat.zeros(size, mask.type());
		mask.copyTo(dst.submat(new Rect(topLeft, new Size(mask.cols(), mask.rows()))));
		return dst ;
	}
}
