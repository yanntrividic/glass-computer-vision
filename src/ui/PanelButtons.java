package ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

public class PanelButtons extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JButton btnPrev ;
	JButton btnNext ;
	JButton btnCompute ;
	
	PanelImage panelImage ;
	
	PanelButtons(PanelImage panelImage, ArrayList<String> imgPaths) {
		setLayout(new FlowLayout()) ;
		this.panelImage = panelImage ;
		
		this.btnPrev = new JButton("<- Previous image");
		btnPrev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { panelImage.updateAfterButton(false); }});
		add(btnPrev);

		this.btnNext = new JButton( "Next image ->" );
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { panelImage.updateAfterButton(true); }});
		add(btnNext);

		this.btnCompute = new JButton("Compute");
		btnCompute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { panelImage.update(true); }});
		add(btnCompute);
	}
}
