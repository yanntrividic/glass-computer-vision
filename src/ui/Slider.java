package ui;
import javax.swing.event.*; 
import javax.swing.*; 

/**
 * Class that inherits from JFrame to make a custom Slider object
 * @author Yann Trividic
 * @version 1.0
 */
class Slider extends JFrame implements ChangeListener { 

	private static final long serialVersionUID = 1L;
	private JSlider slider; 
	private JLabel label;
	private Window win ;
	
	private int beginValue ;
	private int endValue ;
	private int ticks ;
	private int ticksSpacing ;
	private int majorTicksSpacing ;
	
	private String paramName ;
	private int paramType ;

	/**
	 * Constructor of a Slider object
	 * @param win JFrame of the main window
	 * @param beginValue minimum value of the slider
	 * @param endValue maximum value of the slider
	 * @param initValue default value of the slider
	 * @param ticksSpacing spacing between two ticks
	 * @param majorTicksSpacing spacing between bigger ticks
	 * @param paramName parameter name (the one that will be displayed on the GUI)
	 * @param paramType related to the stage of the function it is linked to (integer to specify when to stop the process)
	 */
	public Slider(Window win, int beginValue, int endValue, int initValue, int ticksSpacing, int majorTicksSpacing, String paramName, int paramType) {
		// Create a label
		this.win = win ;
		this.label = new JLabel(); 
		this.paramType = paramType;

		this.beginValue = beginValue ;
		this.endValue = endValue ;
		this.ticks = initValue ;
		this.paramName = paramName ;
		this.ticksSpacing = ticksSpacing ;
		this.majorTicksSpacing = majorTicksSpacing ;
		
		// Create a new JSlider
		this.slider = new JSlider(this.beginValue, this.endValue, this.ticks); 
		
		this.slider.setPaintTrack(true); 
		this.slider.setPaintTicks(true); 
		this.slider.setPaintLabels(true); 

		this.slider.setMajorTickSpacing(this.majorTicksSpacing); 
		this.slider.setMinorTickSpacing(this.ticksSpacing); 

		// Listener linked
		this.slider.addChangeListener(this); 
		
		// Value displayed
		this.label.setText(this.paramName +" = " + this.slider.getValue()); 
	} 

	/**
	 * Updates the slider value
	 */
	public void stateChanged(ChangeEvent e) { 
		this.label.setText(this.paramName +" = " + this.slider.getValue()); 
		this.win.computeImg(this.paramType);
	}

	/**
	 * Getter for the slider
	 * @return the slider, a JSlider object
	 */
	public JSlider getSlider() {
		return slider;
	}

	/**
	 * Getter for the label
	 * @return the label, a JLlider object
	 */
	public JLabel getLabel() {
		return label;
	}

	/**
	 * Getter for the begin value
	 * @return integer minimum value of the slider
	 */
	public int getBeginValue() {
		return beginValue;
	}

	/**
	 * Getter for the end value
	 * @return integer maximum value of the slider
	 */
	public int getEndValue() {
		return endValue;
	} 
}