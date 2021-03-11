package io;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

public class View {
	
	private static final int defaultWidth = 720 ;
	
	/**
	 * Displays an image using HighGui
	 * @param mat Mat object to visualize
	 * @param title title of the window
	 */
	public static void displayImage(Mat mat, String title) {
		displayImage(mat, title, defaultWidth) ;
	}
	
	/**
	 * Displays an image using HighGui
	 * @param mat Mat object to visualize
	 * @param title title of the window
	 */
	public static void displayImage(Mat mat, String title, int displayWidth) {
		int displayHeight = (int) ((double) mat.height() /mat.width()  * displayWidth) ;
		
		//System.out.println(mat.width() + " " + mat.height()) ;
		//System.out.println(displayWidth + " " + displayHeight) ;
		
		Mat resized = new Mat() ;
		Imgproc.resize(mat, resized, new Size(displayWidth, displayHeight));
		
		HighGui.imshow(title, resized);
		HighGui.waitKey();		
	}
}
