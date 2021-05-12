package cv;

import java.util.ArrayList;
import java.util.Collections;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

/**
 * Uses to create mask image of the vessel.
 * 
 * @author Cyril Dubos
 */
public class VesselContour {
    /* STANDARD UTILITIES */

    /**
     * Resizes an image.
     * 
     * @param source the image to be resized
     * @param size   the future size of the longest side
     * @return the resized image
     */
    private static Mat resizeImage(Mat source, int size) {
        Mat destination = new Mat();

        int width = source.width();
        int height = source.height();

        if (width < height) {
            Imgproc.resize(source, destination, new Size(width * size / height, size));
        } else {
            Imgproc.resize(source, destination, new Size(size, height * size / width));
        }

        return destination;
    }

    /**
     * Scales an image.
     * 
     * @param source the image to be scaled
     * @param scale  the scale factor (no change if equal to 1.0)
     * @return the scaled image
     */
    private static Mat scaleImage(Mat source, double scale) {
        Mat destination = new Mat();

        int width = (int) (source.width() * scale);
        int height = (int) (source.height() * scale);

        Imgproc.resize(source, destination, new Size(width, height));

        return destination;
    }

    /**
     * Closes an image with a square structuring element.
     * 
     * @param source the image to be closed
     * @param size   the size of the structuring element
     * @return the closed image
     */
    private static Mat closeImage(Mat source, int size) {
        Mat destination = new Mat();

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(size, size));

        Imgproc.morphologyEx(source, destination, Imgproc.MORPH_CLOSE, kernel);

        return destination;
    }

    /**
     * Opens an image with a square structuring element.
     * 
     * @param source the image to be opened
     * @param size   the size of the structuring element
     * @return the opened image
     */
    private static Mat openImage(Mat source, int size) {
        Mat destination = new Mat();

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(size, size));

        Imgproc.morphologyEx(source, destination, Imgproc.MORPH_OPEN, kernel);

        return destination;
    }

    /**
     * Binarizes an image. All grey pixels (> 0) are converted to white (255).
     * 
     * @param source the image to be binarize
     * @return the binarized image
     */
    private static Mat binarizeImage(Mat source) {
        Mat destination = new Mat();

        Imgproc.threshold(source, destination, 1, 255, Imgproc.THRESH_BINARY);

        return destination;
    }

    /**
     * Inverts an image.
     * 
     * @param source the image to be inverted
     * @return the inverted image
     */
    private static Mat invertImage(Mat source) {
        Mat destination = new Mat();

        Core.bitwise_not(source, destination);

        return destination;
    }

    // https://docs.opencv.org/3.4/d2/d2c/tutorial_sobel_derivatives.html
    private static Mat sobelEdge(Mat src, int ddepth, int ksize, double scale, double delta) {
        Mat grad = new Mat();
        Mat grad_x = new Mat(), grad_y = new Mat();
        Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();

        Imgproc.Sobel(src, grad_x, ddepth, 1, 0, ksize, scale, delta, Core.BORDER_DEFAULT);
        Imgproc.Sobel(src, grad_y, ddepth, 0, 1, ksize, scale, delta, Core.BORDER_DEFAULT);

        Core.convertScaleAbs(grad_x, abs_grad_x);
        Core.convertScaleAbs(grad_y, abs_grad_y);
        Core.addWeighted(abs_grad_x, 0.2, abs_grad_y, 0.5, 0, grad);

        return grad;
    }

    /**
     * Transforms a labels matrix to a binary image. It will "draw" the given
     * labeled component, in white (255) on a black background (0).
     * 
     * @param labels the labels matrix
     * @param index  the index of the label that will be "draw"
     * @return the label image
     */
    private static Mat getLabelImage(Mat labels, int index) {
        Mat destination = Mat.zeros(labels.size(), CvType.CV_8U);

        for (int i = 0; i < labels.rows(); i++) {
            for (int j = 0; j < labels.cols(); j++) {
                int[] data = new int[1];
                labels.get(i, j, data);

                if (data[0] == index) {
                    byte[] newData = { (byte) 255 };
                    destination.put(i, j, newData);
                }
            }
        }

        return destination;
    }

    /**
     * Gets the index of the biggest label of a stats matrix.
     * 
     * @param stats the stats matrix
     * @return the index
     */
    private static int getBiggestLabelIndex(Mat stats) {
        int[] statsData = new int[5];

        int maxArea = 0;
        int maxIndex = 0;

        for (int i = 1; i < stats.rows(); i++) {
            stats.row(i).get(0, 0, statsData);

            int area = statsData[Imgproc.CC_STAT_AREA];

            if (area > maxArea) {
                maxArea = area;
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    /**
     * Gets the image of the biggest label in an image.
     * 
     * @param source the source image
     * @return the image with only the biggest label
     */
    private static Mat getBiggestLabelImage(Mat source) {
        Mat labels = new Mat(), stats = new Mat(), centroids = new Mat();
        Imgproc.connectedComponentsWithStats(source, labels, stats, centroids, 4);

        // todo: binarisation sur les labels (low, high)

        return getLabelImage(labels, getBiggestLabelIndex(stats));
    }

    /* UTILITIES */

    /**
     * Gets boundaries of the component.
     * 
     * @param image the image
     * @return the boundaries 2D-list (the two values of each lines corresponds to
     *         the start and the finish boundaries: -1 and -2 if there are no
     *         boundaries)
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

    /* Find vessel contour methods */

    /**
     * Returns the Canny-Sobel edge of an image.
     * 
     * @param source    the source image
     * @param threshold the threshold value used for Canny and Sobel edges
     * @return the binary image of Canny-Sobel edge
     */
    private static Mat cannySobelEdge(Mat source, int threshold) {
        // Canny edge
        Mat cannyEdge = new Mat();
        Imgproc.GaussianBlur(source, cannyEdge, new Size(7, 7), 1.1);
        Imgproc.Canny(source, cannyEdge, threshold / 3, threshold);

        // Sobel edge
        Mat sobelEdge = new Mat();
        Imgproc.GaussianBlur(source, sobelEdge, new Size(7, 7), 1.1);
        sobelEdge = sobelEdge(sobelEdge, CvType.CV_16S, 3, 1, 1);
        Imgproc.threshold(sobelEdge, sobelEdge, threshold / 2, 255, Imgproc.THRESH_BINARY); // todo: slider

        // Merge Canny and Sobel edges
        Mat destination = new Mat();

        Core.add(cannyEdge, sobelEdge, destination);

        destination = binarizeImage(destination);
        destination = closeImage(destination, 3); // todo: kernel size

        Imgproc.rectangle(destination, new Point(0, 0), new Point(destination.width() - 1, destination.height() - 1),
                new Scalar(0), 5);

        return destination;
    }

    /**
     * Substracts the background of a binary image.
     * 
     * @param source the source image
     * @param size   the size of the structuring element (used to close the image)
     * @return the binary image with black background
     */
    private static Mat substractBackground(Mat source, int size) {
        Mat destination = binarizeImage(source);

        for (int i = 0; i < 2; i++) {
            destination = getBiggestLabelImage(destination);
            destination = invertImage(destination);
        }

        destination = openImage(destination, size);
        destination = closeImage(destination, size);

        return getBiggestLabelImage(destination);
    }

    /**
     * Removes parallele regions of a binary image (only the largest element of a
     * line is kept).
     * 
     * @param source the binary image
     * @return the image with only one white element for each row
     */
    private static Mat removeParalleleRegions(Mat source) {
        Mat destination = Mat.zeros(source.size(), source.type());

        ArrayList<ArrayList<Integer>> boundaries = getBoundaries(source);

        for (int i = 0; i < boundaries.size(); i++)
            Imgproc.line(destination, new Point(boundaries.get(i).get(0), i), new Point(boundaries.get(i).get(1), i),
                    new Scalar(255));

        return getBiggestLabelImage(destination);
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

    private static Mat findVesselContour(String filename, int size, int threshold, int kernel) {
        Mat image = Imgcodecs.imread(filename);

        Mat grayscale = new Mat();
        Imgproc.cvtColor(image, grayscale, Imgproc.COLOR_RGB2GRAY);

        Mat vessel = findVesselContour(grayscale, threshold, kernel);

        return resizeImage(vessel, size);
    }

    public static Mat findVesselContour(Mat mat, int threshold, int kernel) {
        // https://fr.mathworks.com/help/images/ref/adapthisteq.html
        CLAHE clahe = Imgproc.createCLAHE(0.01, new Size(8, 8));
        clahe.apply(mat, mat);

        Mat vessel = cannySobelEdge(mat, threshold);
        vessel = substractBackground(vessel, kernel);
        vessel = removeParalleleRegions(vessel);
        vessel = symetrizeVessel(vessel);

        return vessel;
    }


	// TODO: DELETE EVENTUALLY
    
    public static void main(String[] args) {
        nu.pattern.OpenCV.loadLocally();

        // int size = 500;
        // int threshold = (int) (255 * 0.7);
        // Mat image = findVesselContour("src/resources/img/train/6.jpg", 0, size,
        // threshold, 0);

        int size = 256;
        int kernel = 15;
        int threshold = (int) (255 * 0.22 * 2);
        Mat image = findVesselContour("src/resources/0.jpg", size, threshold, kernel);

        HighGui.imshow(null, image);
        HighGui.waitKey();
    }
}
