package ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Class to implement the bottom panel of the app
 * @author Yann Trividic
 * @version 1.0
 */
public class PanelButtons extends JPanel {

	private static final long serialVersionUID = 1L;
	
	JButton btnPrev ;
	JButton btnNext ;
	JButton btnSeeOriginal ;
	JButton btnCrop ;
	JButton btnMask ;
	JButton btnCompute ;
	JButton btnComputeNext ;
	JButton btnComputePrev ;
	JButton btnReset ;
	
	Window parent ;
	
	/**
	 * Constructor of the PanelButtons class
	 * @param parent JFrame object of the main wnndow
	 */
	PanelButtons(Window parent) {
		setLayout(new FlowLayout()) ;
		this.parent = parent ;
		
		this.btnPrev = new JButton("<- Previous");
		btnPrev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.updateAfterButton(false);
			}});
		add(btnPrev);
	
		this.btnComputePrev = new JButton("<- Compute previous");
		btnComputePrev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.updateAfterButton(false); 
			parent.computeImg(); }});
		add(btnComputePrev);
		
		this.btnSeeOriginal = new JButton("Show original");
		btnSeeOriginal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.showOriginalImg(); }});
		add(btnSeeOriginal);		
		
		this.btnCrop = new JButton("Crop image");
		btnCrop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.computeImg(Window.CROPPING_STAGE); }});
		add(btnCrop);
		
		this.btnMask = new JButton("Compute mask");
		btnMask.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.computeImg(Window.MASKING_STAGE); }});
		add(btnMask);
		
		this.btnCompute = new JButton("Compute");
		btnCompute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.computeImg(); }});
		add(btnCompute);		

		this.btnComputeNext = new JButton( "Compute next ->" );
		btnComputeNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.updateAfterButton(true); parent.computeImg();}});
		add(btnComputeNext);
		
		this.btnNext = new JButton( "Next ->" );
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.updateAfterButton(true); }});
		add(btnNext);

		this.btnReset = new JButton( "Reset parameters" );
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { parent.resetParameters(); }});
		add(btnReset);
	}
}
