import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import cv.PreProcessing;
import io.Reader;

/**
 * Main class of the program
 * @author Yann Trividic
 * @version 1.0
 */

public class Main {
	public static void main(String [] args) throws IOException {
		nu.pattern.OpenCV.loadLocally(); // loads opencv on the machine. Cleaned by garbage collector on shutdown.

		String imgPath = Reader.getImgDir() ;
		
		Mat test_img = Imgcodecs.imread(imgPath+"0.jpg") ; // loads image
		test_img = PreProcessing.rgbToGrayScale(test_img) ;
		test_img = PreProcessing.equalizeGrayMat(test_img) ;
		
		HighGui.imshow("0.png", test_img); // displays test.png
		HighGui.waitKey(); // waits before executing the rest	
	}
}