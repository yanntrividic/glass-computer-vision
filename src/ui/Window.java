package ui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import cv.PreProcessing;
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


	public Window() {
		super("Glass CV" );
		this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		this.setSize(1280, 720);
		this.setLocationRelativeTo( null );

		this.imgIndex = 0 ;
		this.imgPath = Reader.getImgDir("train") ;
		this.imgs = Reader.getAllImgInFolder(imgPath) ;


		Mat mask = Imgcodecs.imread(Reader.getResourcesDir()+"gaussian_distribution.jpg") ;
		mask = PreProcessing.rgbToGrayScale(mask) ;

		// in order to make it on top of each other :
		// http://www.mathcs.emory.edu/~cheung/Courses/377/Syllabus/8-JDBC/GUI/layout.html

		imagePane = (JPanel) this.getContentPane();
		imagePane.setLayout(new FlowLayout());
		this.imageLabel = new JLabel(displayedImage) ;
		this.imagePane.add(new JLabel(displayedImage), "North") ;
		updateImg();
		


		JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout( new FlowLayout() );

		JButton btnPrev = new JButton( "<- Previous image" );
		btnPrev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(imgIndex > 0) imgIndex--;
				updateImg();
				System.out.println("prev clicked");
			}
		});

		contentPane.add(btnPrev, "South");

		JButton btnNext = new JButton( "Next image ->" );
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(imgIndex < imgs.size()) imgIndex++;
				updateImg();
				System.out.println("next clicked");
			}
		});
		contentPane.add(btnNext, "South");

		JButton btnCompute = new JButton( "Compute" );
		contentPane.add(btnCompute, "South");
	}

	public void updateImg() {
		Mat img = Imgcodecs.imread(this.imgPath+this.imgs.get(this.imgIndex)) ; // loads image
		this.imagePane.remove(this.imageLabel);
		this.displayedImage= new ImageIcon(Utils.createAwtImage(img));
		this.imageLabel = new JLabel(displayedImage) ;
		this.imagePane.add(this.imageLabel, "North") ;
		this.imagePane.revalidate();
		this.imagePane.repaint() ;
		System.out.println(this.imgIndex+": "+this.imgPath+this.imgs.get(this.imgIndex));
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