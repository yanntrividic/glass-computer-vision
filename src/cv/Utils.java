package cv;

import org.opencv.core.Mat;
import org.opencv.core.Point;

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
	
	
}
