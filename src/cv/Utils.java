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
			if((int) img[i]+128 < min) min = img[i]+128 ;
			if((int) img[i]+128 > max) max = img[i]+128 ;
		}
		return new int [] {min, max} ;
	}
	
	// TODO: This method is the same as above, it's just a lot more heavy to process, and it works
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
}
