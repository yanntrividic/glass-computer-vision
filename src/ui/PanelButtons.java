package ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class PanelButtons extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JButton btnPrev ;
	JButton btnNext ;
	JButton btnSeeOriginal ;
	JButton btnCompute ;
	JButton btnComputeNext ;
	JButton btnComputePrev ;
	
	Window parent ;
	
	PanelButtons(Window parent) {
		setLayout(new FlowLayout()) ;
		this.parent = parent ;
		
		this.btnPrev = new JButton("<- Previous image");
		btnPrev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.updateAfterButton(false); }});
		add(btnPrev);
	
		this.btnComputePrev = new JButton("<- Compute previous image");
		btnComputePrev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.updateAfterButton(false); parent.computeImg(); }});
		add(btnComputePrev);
		
		this.btnSeeOriginal = new JButton("Show original");
		btnSeeOriginal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.showOriginalImg(); }});
		add(btnSeeOriginal);		
		
		this.btnCompute = new JButton("Compute");
		btnCompute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.computeImg(); }});
		add(btnCompute);

		this.btnComputeNext = new JButton( "Compute next image ->" );
		btnComputeNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.updateAfterButton(true); parent.computeImg(); }});
		add(btnComputeNext);
		
		this.btnNext = new JButton( "Next image ->" );
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.updateAfterButton(true); }});
		add(btnNext);

	}
}
