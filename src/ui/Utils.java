package ui;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

/**
 * Utilitary static class for the GUI
 * @author Yann Trividic
 * @version 1.0
 */
public class Utils {

	private static final int defaultWidth = 720 ;
	
	/**
	 * Converts a Mat object into a BufferedImage with the right dimensions for display
	 * @param mat the original Mat object
	 * @return a BufferedImage object
	 */
	public static BufferedImage createAwtImage(Mat mat) {

		int type = 0;
		if (mat.channels() == 1) {
			type = BufferedImage.TYPE_BYTE_GRAY;
		} else if (mat.channels() == 3) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		} else {
			return null;
		}

		int [] dims = resizeForWindow(mat, 1000, 600) ;
		Mat resized = new Mat() ;
		Imgproc.resize(mat, resized, new Size(dims[0], dims[1]));
		
		BufferedImage image = new BufferedImage(dims[0], dims[1], type);
		WritableRaster raster = image.getRaster();
		DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
		byte[] data = dataBuffer.getData();
		resized.get(0, 0, data);

		return image;
	}
	
	/**
	 * Resizes a Mat object to make it fit the GUI window and preserves the ratio
	 * @param mat the Mat object to resize
	 * @param maxWidth maximum width of the resized image
	 * @param maxHeight maximum height of the resized image
	 * @return a resized Mat object
	 */
	private static int[] resizeForWindow(Mat mat, int maxWidth, int maxHeight) {
		int wantedWidth ;
		int wantedHeight ;
		
		double matRatio = (double) mat.width() / mat.height() ;
		double wantedRatio = (double) maxWidth / maxHeight ;
		if(matRatio > wantedRatio) { // we match the width on maxWidth
			wantedWidth = maxWidth ;
			wantedHeight = (int) (wantedWidth * matRatio) ;
		} else { // we match the height
			wantedHeight = maxHeight ;
			wantedWidth = (int) (wantedHeight * matRatio) ;
		}
		
		return new int [] { wantedWidth, wantedHeight } ;
	}
	
	/**
	 * Displays an image using HighGui
	 * @param mat Mat object to visualize
	 * @param title title of the window
	 */
	public static Mat displayImage(Mat mat, String title) {
		return displayImage(mat, title, defaultWidth) ;
	}
	
	/**
	 * Displays an image using HighGui
	 * @param mat Mat object to visualize
	 * @param title title of the window
	 */
	public static Mat displayImage(Mat mat, String title, int displayWidth) {
		Mat resized = cv.PreProcessing.resizeSpecifiedWidth(mat, displayWidth) ;
		
		HighGui.imshow(title, resized);
		HighGui.waitKey();
		return resized ;
	}
}
