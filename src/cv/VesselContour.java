package cv;

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

public class VesselContour {
    /* Utilities */

    private static Mat resizeImage(Mat image, int size) {
        Mat resizedImage = new Mat();

        int width = image.width();
        int height = image.height();

        if (width < height) {
            Imgproc.resize(image, resizedImage, new Size(width * size / height, size));
        } else {
            Imgproc.resize(image, resizedImage, new Size(size, height * size / width));
        }

        return resizedImage;
    }

    private static Mat scaleImage(Mat image, double scale) {
        Mat resizedImage = new Mat();

        int width = (int) (image.width() * scale);
        int height = (int) (image.height() * scale);

        Imgproc.resize(image, resizedImage, new Size(width, height));

        return resizedImage;
    }

    private static Mat closeImage(Mat image, int size) {
        Mat closedImage = new Mat();

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(size, size));

        Imgproc.morphologyEx(image, closedImage, Imgproc.MORPH_CLOSE, kernel);

        return closedImage;
    }

    private static Mat openImage(Mat image, int size) {
        Mat openedImage = new Mat();

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(size, size));

        Imgproc.morphologyEx(image, openedImage, Imgproc.MORPH_OPEN, kernel);

        return openedImage;
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

    private static Mat mergeEdges(Mat cannyEdge, Mat sobelEdge, int threshold) {
        Mat edge = new Mat(cannyEdge.height(), cannyEdge.width(), CvType.CV_8U);

        for (int i = 0; i < edge.height(); i++) {
            for (int j = 0; j < edge.width(); j++) {
                double[] cannyData = cannyEdge.get(i, j);
                double[] sobelData = sobelEdge.get(i, j);

                double[] data = { cannyData[0] + sobelData[0] };

                edge.put(i, j, data);
            }
        }

        return edge;
    }

    private static int[][] getBoundaries(Mat image) {
        int height = image.rows();
        int width = image.cols();

        int[][] boundaries = new int[height][2];

        for (int i = 0; i < height; i++) {
            boolean inBlob = false;
            int start = -1, finish = -1;

            boundaries[i][0] = -1;
            boundaries[i][1] = -2;

            for (int j = 0; j < width; j++) {
                byte[] data = new byte[1];
                image.get(i, j, data);

                int value = data[0];

                if (inBlob == false && value == 0) {
                    inBlob = true;
                    start = j;
                } else if (inBlob == true && value == -1) {
                    inBlob = false;
                    finish = j;

                    if (boundaries[i][1] - boundaries[i][0] <= (finish - start)) {
                        boundaries[i][1] = finish;
                        boundaries[i][0] = start;
                    }
                }
            }
        }

        return boundaries;
    }

    private static Mat binarizeImage(Mat source) {
        Mat destination = Mat.zeros(source.size(), source.type());

        for (int i = 0; i < source.rows(); i++) {
            for (int j = 0; j < source.cols(); j++) {
                byte[] data = new byte[1];
                source.get(i, j, data);

                if (data[0] != 0) {
                    byte[] newData = { (byte) 255 };
                    destination.put(i, j, newData);
                }
            }
        }

        return destination;
    }

    private static Mat invertImage(Mat source) {
        Mat destination = Mat.zeros(source.size(), source.type());

        for (int i = 0; i < source.rows(); i++) {
            for (int j = 0; j < source.cols(); j++) {
                byte[] data = new byte[1];
                source.get(i, j, data);

                if (data[0] == 0) {
                    byte[] newData = { (byte) 255 };
                    destination.put(i, j, newData);
                }
            }
        }

        return destination;
    }

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

    private static Mat getBiggestLabelImage(Mat source) {
        Mat labels = new Mat(), stats = new Mat(), centroids = new Mat();
        Imgproc.connectedComponentsWithStats(source, labels, stats, centroids, 4);

        return getLabelImage(labels, getBiggestLabelIndex(stats));
    }

    private static int[] getAveragesOfBoundaries(int[][] boundaries) {
        int[] averages = new int[boundaries.length];

        for (int i = 0; i < averages.length; i++) {
            averages[i] = Math.round((boundaries[i][0] + boundaries[i][1]) / 2);
        }

        return averages;
    }

    private static int getMaximumIndexFromIntegerArray(int[] array) {
        int index = 0;

        for (int i = 0; i < array.length; i++) {
            if (array[i] > array[index])
                index = i;
        }

        return index;
    }

    /* Find vessel contour methods */

    private static Mat cannySobelEdge(Mat source, int threshold) {
        // Canny edge
        Mat cannyEdge = new Mat();
        Imgproc.GaussianBlur(source, cannyEdge, new Size(7, 7), 1.1);
        Imgproc.Canny(source, cannyEdge, threshold / 3, threshold);

        // Sobel edge
        Mat sobelEdge = new Mat();
        Imgproc.GaussianBlur(source, sobelEdge, new Size(7, 7), 1.1);
        sobelEdge = sobelEdge(sobelEdge, CvType.CV_16S, 3, 1, 1);
        Imgproc.threshold(sobelEdge, sobelEdge, threshold / 4, 255, Imgproc.THRESH_BINARY);

        // Merge Canny and Sobel edges
        Mat destination = new Mat();
        destination = mergeEdges(cannyEdge, sobelEdge, 0);
        destination = binarizeImage(destination);
        destination = closeImage(destination, 3); // todo: kernel size

        return destination;
    }

    private static Mat substractBackground(Mat source) {
        Mat destination = binarizeImage(source);

        for (int i = 0; i < 2; i++) {
            destination = getBiggestLabelImage(destination);
            destination = invertImage(destination);
        }

        destination = openImage(destination, 5);
        destination = closeImage(destination, 5);

        return destination;
    }

    private static Mat removeParalleleRegions(Mat source) {
        int[][] boundaries = getBoundaries(invertImage(source));
        Mat destination = Mat.zeros(source.size(), source.type());

        for (int i = 0; i < boundaries.length; i++)
            Imgproc.line(destination, new Point(boundaries[i][0], i), new Point(boundaries[i][1], i), new Scalar(255));

        return destination;
    }

    private static Mat findCenters(Mat source) {
        int[][] boundaries = getBoundaries(invertImage(source));
        int[] averages = getAveragesOfBoundaries(boundaries);
        Mat destination = Mat.zeros(source.size(), source.type());

        for (int i = 0; i < averages.length; i++) {
            byte[] data = { (byte) 255 };

            if (averages[i] >= 0)
                destination.put(i, averages[i], data);
        }

        return destination;
    }

    private static int findMostFrequentCenter(Mat source) {
        int[][] boundaries = getBoundaries(invertImage(source));
        int[] averages = getAveragesOfBoundaries(boundaries);
        int maximumAverage = averages[getMaximumIndexFromIntegerArray(averages)];
        int[] histogram = new int[maximumAverage + 1];

        for (int i = 0; i < source.height(); i++) {
            int average = averages[i];

            if (average > 0)
                histogram[average] += 1;
        }

        return getMaximumIndexFromIntegerArray(histogram);
    }

    private static Mat findVesselContour(String filename, int symmetryMode, int size, int threshold,
            int segmentationMode) {
        Mat image = Imgcodecs.imread(filename);

        Mat grayscale = new Mat();
        Imgproc.cvtColor(image, grayscale, Imgproc.COLOR_RGB2GRAY);

        Mat vessel = findVesselContour(grayscale, threshold) ;
        return resizeImage(vessel, size);
    }
    
    
    public static Mat findVesselContour(Mat mat, int threshold) {
        // https://fr.mathworks.com/help/images/ref/adapthisteq.html
        CLAHE clahe = Imgproc.createCLAHE(0.01, new Size(8, 8));
        clahe.apply(mat, mat);

        Mat vessel = cannySobelEdge(mat, threshold);
        vessel = substractBackground(vessel);
        vessel = removeParalleleRegions(vessel);

        Mat centers = findCenters(vessel);
        int center = findMostFrequentCenter(centers);

        Imgproc.line(vessel, new Point(center, 0), new Point(center, vessel.height()), new Scalar(127));

        return vessel;
    }

    public static void main(String[] args) {
        nu.pattern.OpenCV.loadLocally();

        // int kernelSize = 7; // openImage/closeImage kernel size

        // int size = 500;
        // int threshold = (int) (255 * 0.7);
        // Mat image = findVesselContour("src/resources/img/train/6.jpg", 0, size,
        // threshold, 0);

        int size = 256;
        int threshold = (int) (255 * 0.22 * 2);
        Mat image = findVesselContour("src/resources/0.jpg", 0, size, threshold, 0);

        HighGui.imshow(null, image);
        HighGui.waitKey();
    }
}
