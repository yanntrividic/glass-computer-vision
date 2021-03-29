package ui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import io.Reader;


/**
 * GUI of the application. The code is based on this tutorial: https://koor.fr/Java/TutorialSwing/first_application.wp
 * @author yann
 *
 */
public class Window extends JFrame {

	private static final long serialVersionUID = -4939544011287453046L;

	public Window() {
		super("Glass CV" );
		this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		this.setSize(1280, 720);
		this.setLocationRelativeTo( null );
		
		String imgPath = Reader.getImgDir() ;
		System.out.println(imgPath+"1.jpg");
		Mat mat = Imgcodecs.imread(imgPath+"1.jpg");
		
		// in order to make it on top of each other :
		// http://www.mathcs.emory.edu/~cheung/Courses/377/Syllabus/8-JDBC/GUI/layout.html

		JPanel imagePane = (JPanel) this.getContentPane();
		imagePane.setLayout(new FlowLayout());
		ImageIcon image = new ImageIcon(Utils.createAwtImage(mat));
		imagePane.add(new JLabel(image), "North") ;

		JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout( new FlowLayout() );

		JButton btnPrev = new JButton( "<- Previous image" );
		contentPane.add(btnPrev, "South");

		JButton btnNext = new JButton( "Next image ->" );
		contentPane.add(btnNext, "South");

		JButton btnCompute = new JButton( "Compute" );
		contentPane.add(btnCompute, "South");
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