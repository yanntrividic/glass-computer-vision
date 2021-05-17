package cv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

public class SpecularReflexion {


	/**
	 * Method that extracts two point that describes the rectangle in which a glass
	 * has been detected by using its reflexive properties (i.e. the specular
	 * reflexions of the glass)
	 * 
	 * @param src                The source image where we look for the specular
	 *                           reflexion
	 * @param intensityThreshold The first binarization (the specular reflexions
	 *                           must be found and the rest left apart
	 * @param contourThreshold   Second binarization, only the biggest gradient
	 *                           values must be kept
	 * @return a couple of points (top left and bottom right that indicate the
	 *         rectangle in which the glass is)
	 *         
	 * @author Yann Trividic
	 */
	public static Point[] findSpecularReflexion(Mat src, double intensityThreshold, double contourThreshold) {
		// TODO: For this part, we will have to base our algorithm on a few assumptions.
		// Maybe we could assume that the object is usually more in the center on the
		// sides ? (floodfill ?)
		// And that the bigger the binerized area is, the less there are chances for it
		// to be a specular reflexion ?
		// The less "clusters" we have, the less we can infer on the position of the
		// glass

		// TODO: Improve the small features extraction by substracting the opened image
		// to the original one

		Mat norm, simpleBin, sum, contour, binContour;
		norm = new Mat();
		simpleBin = new Mat();
		sum = new Mat();
		contour = new Mat();
		binContour = new Mat();

		// First, we normalize the image to be sure we have white pixels for the
		// binarization
		Core.normalize(src, norm, 0, 255, Core.NORM_MINMAX);
		// The result is stored into a matrix
		int threshold = 255;
		do {
			simpleBin = Segmentation.simpleBinarization(norm, threshold, false);
			threshold--;
			// System.out.println(Core.countNonZero(simpleBin)) ;
		} while ((double) Core.countNonZero(simpleBin) / simpleBin.total() < intensityThreshold);
		// We exit the loop once one percent of the image has been included in the
		// binarization

		// We extract the contour of the image
		Core.normalize(ContourUtils.sobelFilter(src), contour, 0, 50, Core.NORM_MINMAX);
		// And remove the lowest values

		// And we binarize the contour
		threshold = 100;
		do {
			binContour = Segmentation.simpleBinarization(contour, threshold, false);
			threshold -= 3;
			// System.out.println(Core.countNonZero(binContour)) ;
		} while ((double) Core.countNonZero(binContour) / binContour.total() < contourThreshold);
		// until it fills a certain portion of the reference image total size

		Imgproc.dilate(binContour, binContour, Mat.ones(3, 3, CvType.CV_32F));
		// View.displayImage(binContour, "contour2");
		// contour = Segmentation.simpleBinarization(contour,
		// contourBinarizationThreshold, false) ;

		// Then we multiply the first binarization and the contours
		Core.multiply(simpleBin, binContour, simpleBin);

		// And finally we add it to the low contrast image
		Core.add(norm, simpleBin, sum);

		int[] offset = { sum.height() / 5, sum.width() / 7, sum.height() / 10, sum.width() / 7 };

		// offset = new int[] {0,0,0,0} ;

		// a rectangle is drawn around the found blobs
		try {
			int[] coor = ProcessingUtils.getMaskBoundaries(simpleBin);
			ProcessingUtils.drawRectFromBoudaries(sum, ProcessingUtils.getMaskBoundaries(simpleBin), offset);
			// ui.Utils.displayImage(sum, "Image found before cropping") ;
			return ProcessingUtils.getTwoCornersPlusBoundaries(sum, coor, offset);
		} catch (Exception e) {
			System.out.println("No glass found.");
			return new Point[] { new Point(0, 0), new Point(src.width() - 1, src.height() - 1) };
		}
	}
}