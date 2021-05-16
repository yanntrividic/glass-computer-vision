package ui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import io.Reader;


/**
 * GUI of the application. The code is based on this tutorial: https://koor.fr/Java/TutorialSwing/first_application.wp
 * @author Yann Trividic
 * @version 1.0
 */
public class Window extends JFrame {

	private static final long serialVersionUID = -4939544011287453046L;

	public int imgIndex ;
	public String imgPath ;
	public ArrayList<String> imgs ;
	public ImageIcon displayedImage ;

	public JPanel imagePane ;
	public JLabel imageLabel ;

	private PanelImage panelImage;
	private PanelButtons panelButtons;
	private PanelParameter panelParameters; 
	private JPanel contentPane;

	public final static int NO_STAGE = 0;
	public final static int CROPPING_STAGE = 1;
	public final static int MASKING_STAGE = 2;

	/**
	 * Constructor of the Window class
	 */
	public Window(String folder) {
		super("Glass CV" );
		this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		this.setSize(1280, 800);
		this.setLocationRelativeTo(null);

		this.imgIndex = 0 ; // 
		this.imgPath = Reader.getImgDir(folder) ;
		this.imgs = Reader.getAllImgInFolder(imgPath) ;

		ArrayList<String> labels = Reader.getAllLabelsInFolder(imgPath) ;
		//Mat mask = Imgcodecs.imread(Reader.getResourcesDir()+"gaussian_distribution.jpg") ;
		//mask = PreProcessing.rgbToGrayScale(mask) ;

		if(imgs.size() != labels.size()) System.err.println("We couldn't find the same amount of images and labels.") ;

		this.contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		this.panelParameters = new PanelParameter(this) ;
		this.contentPane.add(this.panelParameters,"East") ;
		this.panelParameters.setPreferredSize(new Dimension(250, 600));
		
		this.panelImage = new PanelImage(this, this.panelParameters) ;
		this.contentPane.add(this.panelImage,"Center") ;
		
		this.panelButtons = new PanelButtons(this) ;
		this.contentPane.add(this.panelButtons,"South") ;
	}

	/**
	 * Getter the for the imagePath related to the run
	 * @return the image path of the run
	 */
	public String getImgPath() {
		return this.imgPath;
	}
	
	/**
	 * Updates the displayed image with the entire processing
	 */
	public void computeImg() {
		this.panelImage.update(true) ;
	}
	
	/**
	 * Updates the displayed image with the image obtained after the specified stage
	 * @param stage stage of the processing
	 * @see panelImage.update(boolean, int)
	 */
	public void computeImg(int stage) {
		if (stage != Window.NO_STAGE) this.panelImage.update(true, stage) ;
	}
	
	/**
	 * Updates the displayed image after the next or previous button is updated
	 * @param next the button pressed
	 * @see panelImage.update(boolean, int)
	 */
	public void updateAfterButton(boolean next) {
		this.panelImage.updateAfterButton(next);
	}
	
	/**
	 * Shows the original image by updating the panelImage
	 */
	public void showOriginalImg() {
		this.panelImage.update(false);
	}

	/**
	 * Getter for the imgs object
	 * @return an ArrayList of String that contains the path to all images
	 */
	public ArrayList<String> getImgs() {
		return this.imgs;
	}
	
	/**
	 * Getter for the imgs object
	 * @return an ArrayList of String that contains the path to all images
	 */
	public int getImgIndex() {
		return this.imgIndex;
	}
}