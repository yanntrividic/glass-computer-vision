import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import cv.Extractor;
import cv.PreProcessing;
import io.*;

/**
 * Main class of the program
 * @author Yann Trividic
 * @version 1.0
 */

public class Main {
	public static void main(String [] args) throws IOException {
		nu.pattern.OpenCV.loadLocally(); // loads opencv on the machine. Cleaned by garbage collector on shutdown.

		String imgPath = Reader.getImgDir() ;
		ArrayList<String> imgs = Reader.getAllImgInFolder(imgPath) ;
		
		for(int i = 0 ; i < 10 ; i++) {
			Mat test_img = Imgcodecs.imread(imgPath+imgs.get(i)) ; // loads image
			test_img = PreProcessing.rgbToGrayScale(test_img) ;
			test_img = PreProcessing.medianFilter(test_img) ;
			//test_img = PreProcessing.equalizeGrayMat(test_img) ;
			//test_img = Extractor.sobelFilter(test_img) ;
			//test_img = Segmentation.simpleBinarization(test_img, 200, false) ;
			// = PostProcessing.opening(test_img, 2) ;
			
			
			Extractor.findSpecularReflexion(test_img) ;
			View.displayImage(test_img, ""+i);
		}
	}
}