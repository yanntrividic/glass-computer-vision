package cv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
//import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

/**
 * Static class with methods to extract characteristics from images.
 * @author Yann Trividic
 * @version 1.0
 */
public class Extractor {
	public static Mat sobelFilter(Mat src) {
		//Creating an empty matrix for the destination image
		Mat horizontalContour = new Mat() ;
		Mat verticalContour = new Mat() ;
		Mat dst = new Mat();	
		
		//Applying sobel derivative with values x:0 y:1
		Imgproc.Sobel(src, horizontalContour, -1, 0, 1);
		//HighGui.imshow("Sobel - x:0 & y:1 ", horizontalContour);
		//HighGui.waitKey();
		
		//Applying sobel derivative with values x:1 y:0
		Imgproc.Sobel(src, verticalContour, -1, 1, 0);
		//HighGui.imshow("Sobel - x:1 & y:0 ", verticalContour);
		//HighGui.waitKey();
		
		Core.add(horizontalContour, verticalContour, dst);
		return dst ;
		
	}
	
	public static Mat findSpecularReflexion(Mat src, int specularThreshold, int binarizationThreshold) {
		//TODO: For this part, we will have to base our algorithm on a few assumptions.
		// Maybe we could assume that the object is usually more in the center on the sides ? (floodfill ?)
		// And that the bigger the binerized area is, the less there are chances for it to be a specular reflexion ?
		// The less "clusters" we have, the less we can infer on the position of the glass
		
		
		Mat norm = new Mat();	
		Mat bin = new Mat() ;
		Mat sum = new Mat() ;
		Mat contour = new Mat() ;
		//int [] minMax = Utils.getMinMaxGrayScaleImgVeryExpensive(dst) ;
		//System.out.println("min="+minMax[0]+", max="+minMax[1]) ;
		
		//First, we normalize the image to be sure we have white pixels for the binarization
		Core.normalize(src, norm, 0, 255, Core.NORM_MINMAX);
		//The result is stored into a matrix
		bin = Segmentation.simpleBinarization(norm, specularThreshold, false) ;
		
		//Then we darken it a lot in order to highlight the specular reflexions
		//(it could be any other operation)
		Core.normalize(src, norm, 0, 50, Core.NORM_MINMAX);

		//We extract the contour of the image
		Core.normalize(sobelFilter(src), contour, 0, 255, Core.NORM_MINMAX);
		//And remove the lowest values
		contour = Segmentation.simpleBinarization(contour, binarizationThreshold, false) ;
		
		//Then we multiply the first binarization and the contours
		Core.multiply(bin, contour, bin);
		//And finally we add it to the low contrast image
		Core.add(norm, bin, sum) ;
		
		int[] offset = { sum.height()/10, 
				sum.width()/10,
				sum.height()/10,
				sum.width()/10 } ;
		
		//offset = new int[] {0,0,0,0} ;
		
		Utils.drawRectFromBoudaries(sum, Utils.getMaskBoundaries(bin), offset) ;
		return sum; 
	}
}
