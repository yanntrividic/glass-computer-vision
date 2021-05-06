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
 * @author yann
 *
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
	
	public final static int CROPPING_STAGE = 1;
	public final static int MASKING_STAGE = 2;

	public Window() {
		super("Glass CV" );
		this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		this.setSize(1280, 720);
		this.setLocationRelativeTo(null);

		this.imgIndex = 0 ;
		this.imgPath = Reader.getImgDir("train") ;
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

	public String getImgPath() {
		return imgPath;
	}
	
	public void computeImg() {
		this.panelImage.update(true) ;
	}
	
	public void computeImg(int stage) {
		this.panelImage.update(true, stage) ;
	}
	
	public void updateAfterButton(boolean next) {
		this.panelImage.updateAfterButton(next);
	}
	
	public void showOriginalImg() {
		this.panelImage.update(false);
	}

	public ArrayList<String> getImgs() {
		return imgs;
	}
}