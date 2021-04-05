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
	JButton btnComputeNext ;
	JButton btnComputePrev ;
	
	PanelImage panelImage ;
	
	PanelButtons(PanelImage panelImage, ArrayList<String> imgPaths) {
		setLayout(new FlowLayout()) ;
		this.panelImage = panelImage ;
		
		this.btnPrev = new JButton("<- Previous image");
		btnPrev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { panelImage.updateAfterButton(false); }});
		add(btnPrev);
	
		this.btnComputePrev = new JButton("<- Compute previous image");
		btnComputePrev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { panelImage.updateAfterButton(false); panelImage.update(true); }});
		add(btnComputePrev);
		
		this.btnCompute = new JButton("Compute");
		btnCompute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { panelImage.update(true); }});
		add(btnCompute);

		this.btnComputeNext = new JButton( "Compute next image ->" );
		btnComputeNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { panelImage.updateAfterButton(true); panelImage.update(true); }});
		add(btnComputeNext);
		
		this.btnNext = new JButton( "Next image ->" );
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { panelImage.updateAfterButton(true); }});
		add(btnNext);

	}
}
