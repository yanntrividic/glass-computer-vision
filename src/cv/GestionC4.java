package cv;

import java.awt.Point;

import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class GestionC4 {
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static Mat loadPicture(String path) {
		nu.pattern.OpenCV.loadLocally();
		Imgcodecs imageCodecs =new Imgcodecs();
		Mat matrix=imageCodecs.imread(path);
		return matrix;
	}
	
	public static void drawEllipse() {
		
	      //Reading the source image in to a Mat object
	      Mat src = Imgcodecs.imread("E:\\image\\18.png");
	    //Reading the source image in to a Mat object
	      //Mat src = Imgcodecs.imread("D:\\images\\blank.jpg");
	      //Drawing an Ellipse
	      RotatedRect box = new RotatedRect(new Point(300, 200), new Size(260, 180), 180);
	      Scalar color = new Scalar(64, 64, 64);
	      int thickness = 10;
	      Imgproc.ellipse (src, box, color, thickness);
	      //Imgproc.ellipse(src, new Point(150,150), new Size(260, 180), 25.0, 25.0, 25.0, color); test autres arguments
	      //Saving and displaying the image
	      Imgcodecs.imwrite("arrowed_line.jpg", src);
	      HighGui.imshow("Drawing an ellipse", src);
	      HighGui.waitKey();
	   
	      
	   }
	
	public static void getTheEllipse(Mat img) {
		
	}
	
	
	public static void main(String[]args) {
		drawEllipse();
	}

}
