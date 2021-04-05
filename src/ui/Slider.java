package ui;
import javax.swing.event.*; 
import javax.swing.*; 

class Slider extends JFrame implements ChangeListener { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSlider slider; 
	private JLabel label;
	
	private int beginValue ;
	private int endValue ;
	private int ticks ;
	private int ticksSpacing ;
	private int majorTicksSpacing ;
	
	private String paramName ;

	public Slider(int beginValue, int endValue, int initValue, int ticksSpacing, int majorTicksSpacing, String paramName) {
		// Créer une étiquette
		this.label = new JLabel(); 

		this.beginValue = beginValue ;
		this.endValue = endValue ;
		this.ticks = initValue ;
		this.paramName = paramName ;
		this.ticksSpacing = ticksSpacing ;
		this.majorTicksSpacing = majorTicksSpacing ;
		
		// Créer un slider 
		this.slider = new JSlider(this.beginValue, this.endValue, this.ticks); 
		// Peindre la piste(track) et l'étiquette
		this.slider.setPaintTrack(true); 
		this.slider.setPaintTicks(true); 
		this.slider.setPaintLabels(true); 
		// Définir l'espacement
		this.slider.setMajorTickSpacing(this.majorTicksSpacing); 
		this.slider.setMinorTickSpacing(this.ticksSpacing); 

		// Associer le Listener au slider
		this.slider.addChangeListener(this); 
		// Ajouter le slider au panneau

		// Ajouter le panneau au frame
		// Définir le texte de l'étiquette
		this.label.setText(this.paramName +"=" + this.slider.getValue()); 
	} 

	// Si la valeur du slider est modifiée
	public void stateChanged(ChangeEvent e) { 
		this.label.setText(this.paramName +"=" + this.slider.getValue()); 
	}

	public JSlider getSlider() {
		return slider;
	}

	public JLabel getLabel() {
		return label;
	}

	public int getBeginValue() {
		return beginValue;
	}

	public int getEndValue() {
		return endValue;
	} 
}