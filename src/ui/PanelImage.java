package ui;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import io.Reader;

public class PanelImage extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Mat currentImg ;
	
	private JLabel textLabel ;
	private JLabel imageLabel ;
	
	private String imgFolder ;
	private ArrayList<String> imgFiles ;
	private int currentIndex ;
	
	public PanelImage(String imgFolder) {
		this.imgFolder = imgFolder ;
		this.imgFiles = Reader.getAllImgInFolder(this.imgFolder) ;
		this.currentIndex = 0 ;
		
		setLayout(new BorderLayout());
		this.currentImg = Imgcodecs.imread(imgFolder+imgFiles.get(currentIndex)) ;
		
		this.textLabel = new JLabel() ;
		this.textLabel.setText(imgFiles.get(currentIndex)) ;
		add(this.textLabel,"North") ;
		
		this.imageLabel = getLabelFromMat(currentImg) ;
		add(imageLabel,"South") ;
		System.out.println(this.textLabel.getText());
	}
	
	public void update(boolean compute) {
		remove(this.textLabel) ;
		remove(this.imageLabel) ;
		
		this.textLabel = new JLabel() ;
		this.textLabel.setText((compute?"Computed ":"")+imgFiles.get(currentIndex)) ;
		add(this.textLabel, "North") ;
		
		if(!compute) {
			this.currentImg = Imgcodecs.imread(this.imgFolder+this.imgFiles.get(this.currentIndex)) ;
		} else {
			this.currentImg = cv.Extractor.testUI(this.currentImg) ;
		}

		this.imageLabel = getLabelFromMat(this.currentImg) ;
		add(this.imageLabel, "South") ;
		revalidate();
	}
	
	public void updateAfterButton(boolean next) {
		if(next && this.currentIndex < this.imgFiles.size()) this.currentIndex++ ;
		if(!next && this.currentIndex > 0) this.currentIndex-- ;
		update(false);
	}
	
	private JLabel getLabelFromMat(Mat img) {
		return new JLabel(new ImageIcon(Utils.createAwtImage(img))) ;
	}

	public Mat getCurrentImg() {
		return currentImg;
	}

	public void setCurrentImg(Mat currentImg) {
		this.currentImg = currentImg;
	}

	public String getImgFolder() {
		return imgFolder;
	}

	public void setImgFolder(String imgFolder) {
		this.imgFolder = imgFolder;
	}

	public ArrayList<String> getImgFiles() {
		return imgFiles;
	}

	public void setImgFiles(ArrayList<String> imgFiles) {
		this.imgFiles = imgFiles;
	}
}
