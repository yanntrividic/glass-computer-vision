package ui;
import java.awt.BorderLayout;
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
	private JPanel contentPane;

	public Window() {
		super("Glass CV" );
		this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		this.setSize(1280, 720);
		this.setLocationRelativeTo(null);

		this.imgIndex = 0 ;
		this.imgPath = Reader.getImgDir("train") ;
		this.imgs = Reader.getAllImgInFolder(imgPath) ;

		//Mat mask = Imgcodecs.imread(Reader.getResourcesDir()+"gaussian_distribution.jpg") ;
		//mask = PreProcessing.rgbToGrayScale(mask) ;


		this.contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		System.out.println(imgs.get(0));
		
		this.panelImage = new PanelImage(imgPath) ;
		this.contentPane.add(this.panelImage,"North") ;
		
		this.panelButtons = new PanelButtons(this.panelImage, imgs) ;
		this.contentPane.add(this.panelButtons,"South") ;
	}

	public static void main(String[] args) throws Exception {
		nu.pattern.OpenCV.loadLocally(); //loads opencv for this run
		// Apply a look'n feel

		UIManager.setLookAndFeel( new NimbusLookAndFeel() );

		// Start my window
		Window myWindow = new Window();
		myWindow.setVisible( true );
	}

	
	
	
}