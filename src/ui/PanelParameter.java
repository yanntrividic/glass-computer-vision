package ui;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * from https://waytolearnx.com/2020/05/jslider-java-swing.html
 * 
 * @author yann
 *
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

	public static final String CROPPING_LABEL_STR = "CROPPING PARAMETERS";
	public static final String MASKING_LABEL_STR = "MASKING PARAMETERS";

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

	private Window parent;

	public PanelParameter(Window parent) {
		this.parent = parent;
		setLayout(new FlowLayout());

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
	}

	public int getMedianFilterKSize() {
		int val = this.medianFilterKSizeSlider.getSlider().getValue();
		return (val % 2 == 1 ? val : val + 1);
	}

	public double getAlphaSrc() {
		int val = this.alphaSrcSlider.getSlider().getValue();
		return percentToDecimal(val);
	}

	public double getAlphaMask() {
		int val = this.alphaMaskSlider.getSlider().getValue();
		return percentToDecimal(val);
	}

	public double getIntensity() {
		int val = this.intensityThresholdSlider.getSlider().getValue();
		return perThousandToDecimal(val);
	}

	public double getContour() {
		int val = this.contourThresholdSlider.getSlider().getValue();
		return perThousandToDecimal(val);
	}

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

	public int getThresholdVesselContour() {
		return this.thresholdVesselContour.getSlider().getValue();
	}

	public int getKernelVesselContour() {
		return this.kernelVesselContour.getSlider().getValue();
	}
}
