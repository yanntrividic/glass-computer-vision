package ui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Class that implements the image panel (where the image is displayed in the GUI)
 * @author Yann Trividic
 * @version 1.0
 */
public class PanelImage extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int maxDisplayHeight = 700;
	private static final int maxDisplayWidth = 900;

	private Mat currentImg;
	private Mat computedImg;

	private JLabel imageLabel;

	private Window parent;
	private PanelParameter panelParameter;

	/**
	 * Constructor of the PanelImage object
	 * @param parent JFrame of the main window
	 * @param panelParameter series of sliders to get the parameters of the algorithm to process the glass
	 */
	public PanelImage(Window parent, PanelParameter panelParameter) {
		this.parent = parent;
		this.panelParameter = panelParameter;

		setLayout(new BorderLayout());
		this.currentImg = Imgcodecs.imread(parent.getImgPath() + parent.getImgs().get(parent.getImgIndex()));
		this.computedImg = null;

		this.imageLabel = getLabelFromMat(currentImg);
		add(imageLabel, "Center");
	}

	/**
	 * Performs the full execution of the computeImage algorithm or displays the original image
	 * @param compute boolean, if true : cmputes the image, else : displays the original image
	 */
	public void update(boolean compute) {
		update(compute, -1) ;
	}
	
	/**
	 * Performs the full execution of the computeImage algorithm or displays the original image
	 * @param compute boolean, if true : cmputes the image, else : displays the original image
	 * @param stage integer, the stage at which the process will be stopped
	 */
	public void update(boolean compute, int stage) {
		this.imageLabel.setIcon(null);
				
		if (!compute) {
			this.currentImg = Imgcodecs.imread(parent.getImgPath() + parent.getImgs().get(parent.getImgIndex()));
			this.computedImg = null;
		} else {
			this.computedImg = cv.Extractor.computeImage(stage, this.currentImg, this.parent,
					this.panelParameter.getMedianFilterKSize(),	
					this.panelParameter.getAlphaSrc(), 
					this.panelParameter.getAlphaMask(),	
					this.panelParameter.getIntensity(), 
					this.panelParameter.getContour(), 
					this.panelParameter.getMinimumSurface(), 
					this.panelParameter.getThresholdVesselContour(),
					this.panelParameter.getKernelVesselContour(),
					this.panelParameter.getResizeWidthEllipse(), 
					this.panelParameter.getAngle());
		}

		this.imageLabel = getLabelFromMat(this.computedImg == null ? this.currentImg : this.computedImg);
		add(this.imageLabel, "South");
		revalidate();
	}

	/**
	 * Changes the image to the next one or the previous one
	 * @param next if true : next image, else : previous image
	 */
	public void updateAfterButton(boolean next) {
		if (next && parent.getImgIndex() < parent.getImgs().size() - 1)
			parent.incrementIndex();
		if (!next && parent.getImgIndex() > 0)
			parent.decrementIndex();
		parent.updateText();
		update(false);
	}

	private JLabel getLabelFromMat(Mat img) {
		return new JLabel(new ImageIcon(Utils.createAwtImage(img, maxDisplayWidth, maxDisplayHeight)));
	}

	/**
	 * Getter for the current image
	 * @return the current displayed image
	 */
	public Mat getCurrentImg() {
		return currentImg;
	}

	/**
	 * Setter for the current image
	 * @param currentImg sets the current image
	 */
	public void setCurrentImg(Mat currentImg) {
		this.currentImg = currentImg;
	}
}
