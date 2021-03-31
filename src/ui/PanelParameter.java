package ui;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * from https://waytolearnx.com/2020/05/jslider-java-swing.html
 * @author yann
 *
 */
public class PanelParameter extends JPanel {
	JSlider firstParam ;

	public PanelParameter() {
		setLayout(new FlowLayout()) ;

		this.firstParam = new JSlider();

		JLabel label = new JLabel(); 

		this.firstParam.setPaintTrack(true); 
		this.firstParam.setPaintTicks(true); 
		this.firstParam.setPaintLabels(true); 
		// Définir l'espacement
		this.firstParam.setMajorTickSpacing(20); 
		this.firstParam.setMinorTickSpacing(5); 

		// Associer le Listener au slider
		//this.firstParam.addChangeListener(obj); 
		// Ajouter le slider au panneau
		add(this.firstParam); 

		// Définir le texte de l'étiquette
		label.setText("La valeur du Slider est : " + this.firstParam.getValue()); 

		add(label); 
	}
}
