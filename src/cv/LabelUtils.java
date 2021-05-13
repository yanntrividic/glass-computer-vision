package cv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class LabelUtils {

    /**
     * Transforms a labels matrix to a binary image. It will "draw" the given
     * labeled component, in white (255) on a black background (0).
     * 
     * @param labels the labels matrix
     * @param index  the index of the label that will be "draw"
     * @return the label image
     * @return the label image
     * @author Cyril Dubos
     */
    public static Mat getLabelImage(Mat labels, int index) {
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
     * @author Cyril Dubos
     */
    public static int getBiggestLabelIndex(Mat stats) {
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
     * @author Cyril Dubos
     */
    public static Mat getBiggestLabelImage(Mat source) {
        Mat labels = new Mat(), stats = new Mat(), centroids = new Mat();
        Imgproc.connectedComponentsWithStats(source, labels, stats, centroids, 4);

        // todo: binarisation sur les labels (low, high)

        return getLabelImage(labels, getBiggestLabelIndex(stats));
    }
}
