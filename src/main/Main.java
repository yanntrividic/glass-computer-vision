package main;

import java.io.IOException;
import java.io.File;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

public class Main {
	public static void main(String [] args) throws IOException {
		nu.pattern.OpenCV.loadLocally(); // loads opencv on the machine. Cleaned by garbage collector on shutdown.

		String imgPath = "."+File.separator+"src"+File.separator+"resources"+File.separator+"img"+File.separator ;
		
		Mat test_img = Imgcodecs.imread(imgPath+"0.jpg") ; // loads image
		
		HighGui.imshow("0.png", test_img); // displays test.png
		HighGui.waitKey(); // waits before executing the rest	
	}
}
