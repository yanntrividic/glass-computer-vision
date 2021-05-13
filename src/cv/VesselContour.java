package cv;

import java.util.ArrayList;
import java.util.Collections;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

/**
 * Uses to create mask image of the vessel.
 * 
 * @author Cyril Dubos
 */
public class VesselContour {
    /* UTILITIES */

    /**
     * Gets boundaries of the component.
     * 
     * @param image the image
     * @return the boundaries 2D-list (the two values of each lines corresponds to
     *         the start and the finish boundaries: -1 and -2 if there are no
     *         boundaries)
     * @author Cyril Dubos
     */
    private static ArrayList<ArrayList<Integer>> getBoundaries(Mat image) {
        int height = image.rows();
        int width = image.cols();

        ArrayList<ArrayList<Integer>> boundaries = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < height; i++) {
            ArrayList<Integer> boundary = new ArrayList<Integer>();
            boundary.add(-1);
            boundary.add(-2);

            boundaries.add(boundary);

            boolean inBlob = false;
            int start = -1, finish = -1;

            for (int j = 0; j < width; j++) {
                byte[] data = new byte[1];
                image.get(i, j, data);

                int value = data[0];

                if (inBlob == false && value == -1) {
                    inBlob = true;
                    start = j;
                } else if (inBlob == true && value == 0) {
                    inBlob = false;
                    finish = j;

                    if (boundaries.get(i).get(1) - boundaries.get(i).get(0) <= (finish - start)) {
                        boundary = new ArrayList<Integer>();
                        boundary.add(start);
                        boundary.add(finish);

                        boundaries.set(i, boundary);
                    }
                }
            }
        }

        return boundaries;
    }

    /**
     * Gets centers of boundaries.
     * 
     * @param boundaries the boundaries 2D-list
     * @return the centers list
     * @author Cyril Dubos
     */
    private static ArrayList<Integer> getCenters(ArrayList<ArrayList<Integer>> boundaries) {
        ArrayList<Integer> centers = new ArrayList<Integer>();

        for (ArrayList<Integer> boundary : boundaries)
            centers.add(Math.round((boundary.get(0) + boundary.get(1)) / 2));

        return centers;
    }

    /**
     * Gets the most frequent center of boundaries.
     * 
     * @param center the centers list
     * @return the center
     * @author Cyril Dubos
     */
    private static int getCenter(ArrayList<Integer> centers) {
        int center = centers.get(0);
        int frequency = Collections.frequency(centers, center);

        for (int c : centers) {
            int f = Collections.frequency(centers, c);

            if (center < 0 || (c > 0 && f > frequency)) {
                center = c;
                frequency = f;
            }
        }

        return center;
    }

    /* SECONDARY METHODS */

    /**
     * Substracts the background of a binary image.
     * 
     * @param source the source image
     * @param size   the size of the structuring element (used to close the image)
     * @return the binary image with black background
     * @author Cyril Dubos
     */
    private static Mat substractBackground(Mat source, int size) {
        Mat destination = Segmentation.simpleBinarization(source, 1, false);

        for (int i = 0; i < 2; i++) {
            destination = LabelUtils.getBiggestLabelImage(destination);
            Core.bitwise_not(destination, destination);
        }

        destination = Morphology.opening(destination, size);
        destination = Morphology.closing(destination, size);

        return LabelUtils.getBiggestLabelImage(destination);
    }

    /**
     * Removes parallele regions of a binary image (only the largest element of a
     * line is kept).
     * 
     * @param source the binary image
     * @return the image with only one white element for each row
     * @author Cyril Dubos
     */
    private static Mat removeParalleleRegions(Mat source) {
        Mat destination = Mat.zeros(source.size(), source.type());

        ArrayList<ArrayList<Integer>> boundaries = getBoundaries(source);

        for (int i = 0; i < boundaries.size(); i++)
            Imgproc.line(destination, new Point(boundaries.get(i).get(0), i), new Point(boundaries.get(i).get(1), i),
                    new Scalar(255));

        return LabelUtils.getBiggestLabelImage(destination);
    }

    private static Mat symetrizeVessel(Mat source) {
        Mat destination = Mat.zeros(source.size(), source.type());

        ArrayList<ArrayList<Integer>> boundaries = getBoundaries(source);
        ArrayList<Integer> centers = getCenters(boundaries);
        int center = getCenter(centers);

        for (int i = 0; i < source.height(); i++) {
            int start = boundaries.get(i).get(0);
            int finish = boundaries.get(i).get(1);

            if (start >= 0 && finish >= 0) {
                int left = center - start;
                int right = finish - center;

                if (left > right)
                    Imgproc.line(destination, new Point(start, i), new Point(center + left, i), new Scalar(255));
                else
                    Imgproc.line(destination, new Point(center - right, i), new Point(finish, i), new Scalar(255));
            }
        }

        return destination;
    }

    /* PRIMARY METHOD */

    public static Mat findVesselContour(Mat mat, int threshold, int kernel) {
        // https://fr.mathworks.com/help/images/ref/adapthisteq.html
        CLAHE clahe = Imgproc.createCLAHE(0.01, new Size(8, 8));
        clahe.apply(mat, mat);

        Mat vessel = ContourUtils.cannySobelEdge(mat, threshold, kernel / 5);

        Imgproc.rectangle(vessel, new Point(0, 0), new Point(vessel.width() - 1, vessel.height() - 1), new Scalar(0),
                5);

        vessel = substractBackground(vessel, kernel);
        vessel = removeParalleleRegions(vessel);
        vessel = symetrizeVessel(vessel);

        return vessel;
    }
}
