import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import cv.Extractor;
import cv.PreProcessing;
import cv.Utils;
import io.*;

/**
 * Main class of the program
 * @author Yann Trividic
 * @version 1.0
 */

public class Main {
	public static void main(String [] args) throws IOException {
		nu.pattern.OpenCV.loadLocally(); //loads opencv for this run

		String imgPath = Reader.getImgDir() ;
		ArrayList<String> imgs = Reader.getAllImgInFolder(imgPath) ;

		//Mat gauss = Imgcodecs.imread(Reader.getResourcesDir()+"gaussian_distribution.png") ;
		//gauss = PreProcessing.meanFilter(gauss, 80) ;
		//Core.normalize(gauss, gauss, 0, 255, Core.NORM_MINMAX);
		
		//gauss = View.displayImage(gauss, "gaussian");
		//Writer.matToJPG(gauss, Reader.getResourcesDir()+"gaussian_distribution");

		Mat mask = Imgcodecs.imread(Reader.getResourcesDir()+"gaussian_distribution.jpg") ;
		mask = PreProcessing.rgbToGrayScale(mask) ;
		//Core.normalize(mask, mask, 0, 255, Core.NORM_MINMAX);
		
		for(int i = 30 ; i < 70 ; i++) {
			Mat test_img = Imgcodecs.imread(imgPath+imgs.get(i)) ; // loads image
			test_img = PreProcessing.rgbToGrayScale(test_img) ;
			test_img = Utils.applyMask(test_img, 1.0, mask, 0.3, 0) ;
			test_img = PreProcessing.medianFilter(test_img, 5) ;
			//test_img = PreProcessing.equalizeGrayMat(test_img) ;
			//test_img = Extractor.sobelFilter(test_img) ;
			//test_img = Segmentation.simpleBinarization(test_img, 200, false) ;
			//test_img = PostProcessing.opening(test_img, 2) ;

			test_img = Extractor.findSpecularReflexion(test_img, 240, 20) ;
			View.displayImage(test_img, ""+i);
		}
	}
}