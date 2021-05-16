package ui;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Class to handle the various parameters for our image processing algorithm
 * from https://waytolearnx.com/2020/05/jslider-java-swing.html
 * 
 * @author Yann Trividic
 * @version 1.0
 */

public class PanelParameter extends JPanel {

	public static final int DEFAULT_MEDIAN_FILTER = 3;
	public static final int DEFAULT_ALPHA_SRC = 90;
	public static final int DEFAULT_ALPHA_MASK = 60;
	public static final int DEFAULT_INTENSITY_THRESHOLD = 15;
	public static final int DEFAULT_CONTOUR_THRESHOLD = 9;
	public static final int DEFAULT_MINIMUM_SURFACE = 80;
	public static final int DEFAULT_THRESHOLD_VESSEL_CONTOUR = 15;
	public static final int DEFAULT_KERNEL_VESSEL_CONTOUR = 5;
	public static final int DEFAULT_RESIZE_WIDTH_ELLIPSE = 400;
	public static final int DEFAULT_INCLI = 1;

	public static final String CROPPING_LABEL_STR = "CROPPING PARAMETERS";
	public static final String MASKING_LABEL_STR = "MASKING PARAMETERS";
	public static final String ELLIPSE_LABEL_STR = "ELLIPSE PARAMETERS";

	private static final long serialVersionUID = 1L;

	private JLabel croppingParamsLabel;
	private Slider medianFilterKSizeSlider;
	private Slider alphaSrcSlider;
	private Slider alphaMaskSlider;

	private Slider intensityThresholdSlider;
	private Slider contourThresholdSlider;
	private Slider minimumSurfaceSlider;

	private JLabel maskingParamsLabel;
	private Slider thresholdVesselContour;
	private Slider kernelVesselContour;
	
	private JLabel findingEllipseParamsLabel;
	private Slider resizeWidth;
	private Slider angle;

	private Window parent;

	/**
	 * Constructor for the PanelParameter object
	 * @param parent JFrame object of the main wnndow
	 */
	public PanelParameter(Window parent) {
		this.parent = parent;
		setLayout(new FlowLayout());

		// CROPPING IMAGE PARAMETERS
		this.croppingParamsLabel = new JLabel();
		this.croppingParamsLabel.setText(CROPPING_LABEL_STR);
		add(this.croppingParamsLabel);

		this.medianFilterKSizeSlider = new Slider(parent, 0, 10, DEFAULT_MEDIAN_FILTER, 1, 5, "medianFilterKSize",
				Window.CROPPING_STAGE);
		add(medianFilterKSizeSlider.getLabel());
		add(medianFilterKSizeSlider.getSlider());

		this.alphaSrcSlider = new Slider(parent, 0, 100, DEFAULT_ALPHA_SRC, 10, 50, "alphaSrc", Window.CROPPING_STAGE);
		add(alphaSrcSlider.getLabel());
		add(alphaSrcSlider.getSlider());

		this.alphaMaskSlider = new Slider(parent, 0, 100, DEFAULT_ALPHA_MASK, 10, 50, "alphaMask",
				Window.CROPPING_STAGE);
		add(alphaMaskSlider.getLabel());
		add(alphaMaskSlider.getSlider());

		this.intensityThresholdSlider = new Slider(parent, 0, 100, DEFAULT_INTENSITY_THRESHOLD, 10, 50,
				"intensityThreshold", Window.CROPPING_STAGE);
		add(intensityThresholdSlider.getLabel());
		add(intensityThresholdSlider.getSlider());

		this.contourThresholdSlider = new Slider(parent, 0, 100, DEFAULT_CONTOUR_THRESHOLD, 10, 50, "contourThreshold",
				Window.CROPPING_STAGE);
		add(contourThresholdSlider.getLabel());
		add(contourThresholdSlider.getSlider());

		this.minimumSurfaceSlider = new Slider(parent, 0, 100, DEFAULT_MINIMUM_SURFACE, 10, 50, "minimumSurface",
				Window.CROPPING_STAGE);
		add(minimumSurfaceSlider.getLabel());
		add(minimumSurfaceSlider.getSlider());

		
		// FINDING MASK PARAMETERS
		
		this.maskingParamsLabel = new JLabel();
		this.maskingParamsLabel.setText(MASKING_LABEL_STR);
		add(this.maskingParamsLabel);

		this.thresholdVesselContour = new Slider(parent, 0, 100, DEFAULT_THRESHOLD_VESSEL_CONTOUR, 10, 50,
				"thresholdVesselContour", Window.MASKING_STAGE);
		add(thresholdVesselContour.getLabel());
		add(thresholdVesselContour.getSlider());

		this.kernelVesselContour = new Slider(parent, 0, 16, DEFAULT_KERNEL_VESSEL_CONTOUR, 2, 8, "kernelVesselContour",
				Window.MASKING_STAGE);
		add(kernelVesselContour.getLabel());
		add(kernelVesselContour.getSlider());
		
		
		// FINDING ELLIPSE PARAMETERS
		
		this.findingEllipseParamsLabel = new JLabel();
		this.findingEllipseParamsLabel.setText(ELLIPSE_LABEL_STR);
		add(this.findingEllipseParamsLabel);
		
		this.resizeWidth = new Slider(parent, 350, 600, DEFAULT_RESIZE_WIDTH_ELLIPSE, 50, 50, "resizeWidth",
				Window.NO_STAGE);
		add(resizeWidth.getLabel());
		add(resizeWidth.getSlider());
		
		this.angle = new Slider(parent, 1, 3, DEFAULT_INCLI, 1, 1, "glassInclination",
				Window.NO_STAGE);
		add(angle.getLabel());
		add(angle.getSlider()); 
	}

	public void resetParameters() {
		medianFilterKSizeSlider.reset();
		alphaSrcSlider.reset();
		alphaMaskSlider.reset();

		intensityThresholdSlider.reset();
		contourThresholdSlider.reset();
		minimumSurfaceSlider.reset();

		thresholdVesselContour.reset();
		kernelVesselContour.reset();

		resizeWidth.reset();
		angle.reset();
	}
	
	/**
	 * Getter for the median filter kernel size paramter
	 * @return an odd integer between 1 and 11
	 */
	public int getMedianFilterKSize() {
		int val = this.medianFilterKSizeSlider.getSlider().getValue();
		return (val % 2 == 1 ? val : val + 1);
	}

	/**
	 * Getter for the alpha channel parameter of the source image 
	 * @return a percentage between 0 and 100
	 */
	public double getAlphaSrc() {
		int val = this.alphaSrcSlider.getSlider().getValue();
		return percentToDecimal(val);
	}

	/**
	 * Getter for the alpha channel parameter of the mask image
	 * @return a percentage between 0 and 100
	 */
	public double getAlphaMask() {
		int val = this.alphaMaskSlider.getSlider().getValue();
		return percentToDecimal(val);
	}

	/**
	 * Getter for the intensity threshold parameter
	 * @return a percentage between 0 and 100
	 */
	public double getIntensity() {
		int val = this.intensityThresholdSlider.getSlider().getValue();
		return perThousandToDecimal(val);
	}

	/**
	 * Getter for the contour threshold parameter
	 * @return a percentage between 0 and 100
	 */
	public double getContour() {
		int val = this.contourThresholdSlider.getSlider().getValue();
		return perThousandToDecimal(val);
	}

	/**
	 * Getter for the minimum surface threshold parameter
	 * @return a percentage between 0 and 100
	 */
	public double getMinimumSurface() {
		int val = this.minimumSurfaceSlider.getSlider().getValue();
		return percentToDecimal(val);
	}

	private double percentToDecimal(int percentage) {
		return (double) (percentage) / 100;
	}

	private double perThousandToDecimal(int percentage) {
		return (double) (percentage) / 1000;
	}

	/**
	 * Getter for the vessel contour threshold parameter
	 * @return a percentage between 0 and 100
	 */
	public int getThresholdVesselContour() {
		return this.thresholdVesselContour.getSlider().getValue();
	}

	/**
	 * Getter for the kernel size parameter for the vessel contour 
	 * @return a percentage between 0 and 16
	 */
	public int getKernelVesselContour() {
		return this.kernelVesselContour.getSlider().getValue();
	}
	
	/**
	 * Getter for the getResizeWidthEllipse 
	 * @return an int between 200 and 550
	 */
	public int getResizeWidthEllipse() {
		return this.resizeWidth.getSlider().getValue();
	}
	
	/**
	 * Getter for the getAngle
	 * @return an int between 1 and 3
	 */
	public int getAngle() {
		return this.angle.getSlider().getValue();
	}
}
