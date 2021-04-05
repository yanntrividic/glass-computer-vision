package ui;

import java.awt.FlowLayout;

import javax.swing.JPanel;

/**
 * from https://waytolearnx.com/2020/05/jslider-java-swing.html
 * @author yann
 *
 */
public class PanelParameter extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Slider medianFilterKSizeSlider ;
	private Slider alphaSrcSlider ;
	private Slider alphaMaskSlider ;
	private Slider gammaSlider ;
	
	private Slider intensityThresholdSlider ;
	private Slider contourThresholdSlider ;
	private Slider minimumSurfaceSlider ;

	public PanelParameter() {
		setLayout(new FlowLayout()) ;
		
		this.medianFilterKSizeSlider = new Slider(0, 20, 3, 2, 10, "medianFilterKSizeSlider") ;
		add(medianFilterKSizeSlider.getSlider()) ;
		add(medianFilterKSizeSlider.getLabel()) ;
		
		this.alphaSrcSlider = new Slider(0, 100, 90, 10, 50, "alphaSrcSlider") ;
		add(alphaSrcSlider.getSlider()) ;
		add(alphaSrcSlider.getLabel()) ;	
		
		this.alphaMaskSlider = new Slider(0, 100, 30, 10, 50, "alphaMaskSlider") ;
		add(alphaMaskSlider.getSlider()) ;
		add(alphaMaskSlider.getLabel()) ;	
		
		this.gammaSlider = new Slider(0, 100, 0, 10, 50, "gammaSlider") ;
		add(gammaSlider.getSlider()) ;
		add(gammaSlider.getLabel()) ;	
		
		this.intensityThresholdSlider = new Slider(0, 100, 7, 10, 50, "intensityThreshold") ;
		add(intensityThresholdSlider.getSlider()) ;
		add(intensityThresholdSlider.getLabel()) ;	

		this.contourThresholdSlider = new Slider(0, 100, 2, 10, 50, "contourThreshold") ;
		add(contourThresholdSlider.getSlider()) ;
		add(contourThresholdSlider.getLabel()) ;	
		
		this.minimumSurfaceSlider = new Slider(0, 100, 80, 10, 50, "minimumSurface") ;
		add(minimumSurfaceSlider.getSlider()) ;
		add(minimumSurfaceSlider.getLabel()) ;	
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
	
	public double getGamma() {
		int val = this.gammaSlider.getSlider().getValue() ;
		return percentToDecimal(val) ;
	}
	
	public double getIntensity() { // FIXME: doesn't seem to work
		int val = this.intensityThresholdSlider.getSlider().getValue() ;
		return perThousandToDecimal(val) ;
	}

	public double getContour() { // FIXME: doesn't seem to work
		int val = this.contourThresholdSlider.getSlider().getValue() ;
		return perThousandToDecimal(val) ;
	}
	
	public double getMinimumSurface() { // FIXME: doesn't seem to work
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
