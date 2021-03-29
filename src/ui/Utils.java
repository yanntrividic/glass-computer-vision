package ui;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Utils {

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
}
