import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;
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
		
		for(int i = 0 ; i < imgs.size() ; i++) {
			Mat test_img = Imgcodecs.imread(imgPath+imgs.get(i)) ; // loads image
			Mat grayScale = PreProcessing.rgbToGrayScale(test_img) ;
			grayScale = PreProcessing.medianFilter(grayScale, 5) ;
			
			test_img = Utils.applyMask(grayScale, 0.9, mask, 0.4, 0) ;
			
			//test_img = PreProcessing.equalizeGrayMat(test_img) ;
			//test_img = Extractor.sobelFilter(test_img) ;
			//test_img = Segmentation.simpleBinarization(test_img, 200, false) ;
			//test_img = PostProcessing.opening(test_img, 2) ;

			Point [] points = Extractor.findSpecularReflexion(test_img, 0.007, 0.002) ;
			
			Mat croppedImg = Utils.getCroppedImageFromTopLeftBotRight(grayScale, points[0], points[1]) ;
			View.displayImage(croppedImg, ""+imgs.get(i));
			
			// FIXME: le View.displayImage dans la méthode n'affiche pas les labels 
			// pour l'instant ta méthode a une signature void, du coup on ne peut pas utiliser displayImage
			// depuis ce fichier pour afficher ton masque j'imagine
			Reader.extractLabelsFromJSON(imgPath + imgs.get(i));
		}
	}
}