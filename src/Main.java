import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;
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
		nu.pattern.OpenCV.loadLocally(); //loads opencv for this run

		String imgPath = Reader.getImgDir("train") ;
		ArrayList<String> imgs = Reader.getAllImgInFolder(imgPath) ;
		ArrayList<String> labels = Reader.getAllLabelsInFolder(imgPath) ;

		if(imgs.size() != labels.size()) System.err.println("We couldn't find the same amount of images and labels.") ;
		
		//Mat gauss = Imgcodecs.imread(Reader.getResourcesDir()+"gaussian_distribution.png") ;
		//gauss = PreProcessing.meanFilter(gauss, 80) ;
		//Core.normalize(gauss, gauss, 0, 255, Core.NORM_MINMAX);
		
		//gauss = View.displayImage(gauss, "gaussian");
		//Writer.matToJPG(gauss, Reader.getResourcesDir()+"gaussian_distribution");

		Mat mask = Imgcodecs.imread(Reader.getResourcesDir()+"gaussian_distribution.jpg") ;
		mask = PreProcessing.rgbToGrayScale(mask) ;
		//Core.normalize(mask, mask, 0, 255, Core.NORM_MINMAX);
		
		for(int i = 0 ; i < imgs.size() ; i++) {
			Mat testImg = Imgcodecs.imread(imgPath+imgs.get(i)) ; // loads image
			testImg = PreProcessing.resizeSpecifiedWidth(testImg, 500) ;
			Mat grayScale = PreProcessing.rgbToGrayScale(testImg) ;
			grayScale = PreProcessing.medianFilter(grayScale, 5) ;
			
			testImg = cv.Utils.applyMask(grayScale, 0.9, mask, 0.4, 0) ;
			
			//test_img = PreProcessing.equalizeGrayMat(test_img) ;
			//test_img = Extractor.sobelFilter(test_img) ;
			//test_img = Segmentation.simpleBinarization(test_img, 200, false) ;
			//test_img = PostProcessing.opening(test_img, 2) ;

			Point [] points = Extractor.findSpecularReflexion(testImg, 0.007, 0.002) ;
			
			Mat croppedImg = cv.Utils.getCroppedImageFromTopLeftBotRight(grayScale, points[0], points[1], 0.8) ;
			ui.Utils.displayImage(croppedImg, ""+imgs.get(i));
			ui.Utils.displayImage(Reader.extractLabelsFromJSON(imgPath+ labels.get(i)), "Labels");
		}
	}
}