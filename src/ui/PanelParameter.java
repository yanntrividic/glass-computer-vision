package ui;

import java.awt.FlowLayout;

import javax.swing.JPanel;

/**
 * from https://waytolearnx.com/2020/05/jslider-java-swing.html
 * @author yann
 *
 */


public class PanelParameter extends JPanel {

	public static final int DEFAULT_MEDIAN_FILTER = 3 ;
	public static final int DEFAULT_ALPHA_SRC = 90 ;
	public static final int DEFAULT_ALPHA_MASK = 60 ;
	public static final int DEFAULT_INTENSITY_THRESHOLD = 15 ;
	public static final int DEFAULT_CONTOUR_THRESHOLD = 9 ;
	public static final int DEFAULT_MINIMUM_SURFACE = 80 ;
	
	private static final long serialVersionUID = 1L;
	
	private Slider medianFilterKSizeSlider ;
	private Slider alphaSrcSlider ;
	private Slider alphaMaskSlider ;

	private Slider intensityThresholdSlider ;
	private Slider contourThresholdSlider ;
	private Slider minimumSurfaceSlider ;
	
	private Window parent ;
	
	public PanelParameter(Window parent) {
		this.parent = parent ;
		setLayout(new FlowLayout()) ;
		
		this.medianFilterKSizeSlider = new Slider(parent, 0, 10, DEFAULT_MEDIAN_FILTER, 1, 5, "medianFilterKSize") ;
		add(medianFilterKSizeSlider.getLabel()) ;
		add(medianFilterKSizeSlider.getSlider()) ;
		
		this.alphaSrcSlider = new Slider(parent, 0, 100, DEFAULT_ALPHA_SRC, 10, 50, "alphaSrc") ;
		add(alphaSrcSlider.getLabel()) ;	
		add(alphaSrcSlider.getSlider()) ;
		
		this.alphaMaskSlider = new Slider(parent, 0, 100, DEFAULT_ALPHA_MASK, 10, 50, "alphaMask") ;
		add(alphaMaskSlider.getLabel()) ;	
		add(alphaMaskSlider.getSlider()) ;
				
		this.intensityThresholdSlider = new Slider(parent, 0, 100, DEFAULT_INTENSITY_THRESHOLD, 10, 50, "intensityThreshold") ;
		add(intensityThresholdSlider.getLabel()) ;	
		add(intensityThresholdSlider.getSlider()) ;

		this.contourThresholdSlider = new Slider(parent, 0, 100, DEFAULT_CONTOUR_THRESHOLD, 10, 50, "contourThreshold") ;
		add(contourThresholdSlider.getLabel()) ;	
		add(contourThresholdSlider.getSlider()) ;
		
		this.minimumSurfaceSlider = new Slider(parent, 0, 100, DEFAULT_MINIMUM_SURFACE, 10, 50, "minimumSurface") ;
		add(minimumSurfaceSlider.getLabel()) ;	
		add(minimumSurfaceSlider.getSlider()) ;
	}
	
	public int getMedianFilterKSize() {
		int val = this.medianFilterKSizeSlider.getSlider().getValue() ;
		return (val%2 == 1?val:val+1) ;
	}
	
	public double getAlphaSrc() {
		int val = this.alphaSrcSlider.getSlider().getValue() ;
		return percentToDecimal(val) ;
	}
	
	public double getAlphaMask() {
		int val = this.alphaMaskSlider.getSlider().getValue() ;
		return percentToDecimal(val) ;
	}
	
	public double getIntensity() {
		int val = this.intensityThresholdSlider.getSlider().getValue() ;
		return perThousandToDecimal(val) ;
	}

	public double getContour() {
		int val = this.contourThresholdSlider.getSlider().getValue() ;
		return perThousandToDecimal(val) ;
	}
	
	public double getMinimumSurface() {
		int val = this.minimumSurfaceSlider.getSlider().getValue() ;
		return percentToDecimal(val) ;
	}
	
	private double percentToDecimal(int percentage) {
		return (double) (percentage)/100 ;
	}
	
	private double perThousandToDecimal(int percentage) {
		return (double) (percentage)/1000 ;
	}
}
