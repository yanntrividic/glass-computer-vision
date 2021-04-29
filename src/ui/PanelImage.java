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

	private Mat currentImg;
	private Mat computedImg;

	private JLabel textLabel;
	private JLabel imageLabel;

	private int currentIndex;

	private Window parent;
	private PanelParameter panelParameter;

	public PanelImage(Window parent, PanelParameter panelParameter) {
		this.parent = parent;
		this.panelParameter = panelParameter;
		this.currentIndex = 0;

		setLayout(new BorderLayout());
		this.currentImg = Imgcodecs.imread(parent.getImgPath() + parent.getImgs().get(currentIndex));
		this.computedImg = null;

		this.textLabel = new JLabel();
		this.textLabel.setText(parent.getImgs().get(currentIndex));
		add(this.textLabel, "North");

		this.imageLabel = getLabelFromMat(currentImg);
		add(imageLabel, "South");
	}

	public void update(boolean compute) {
		remove(this.textLabel);
		remove(this.imageLabel);

		this.textLabel = new JLabel();
		this.textLabel.setText((compute ? "Computed " : "") + parent.getImgs().get(currentIndex));
		add(this.textLabel, "North");

		if (!compute) {
			this.currentImg = Imgcodecs.imread(parent.getImgPath() + parent.getImgs().get(this.currentIndex));
			this.computedImg = null;
		} else {
			this.computedImg = cv.Extractor.computeImage(this.currentImg, this.panelParameter.getMedianFilterKSize(),
					this.panelParameter.getAlphaSrc(), this.panelParameter.getAlphaMask(),
					this.panelParameter.getIntensity(), this.panelParameter.getContour(),
					this.panelParameter.getMinimumSurface(), this.panelParameter.getThresholdVesselContour(),
					this.panelParameter.getKernelVesselContour());
		}

		this.imageLabel = getLabelFromMat(this.computedImg == null ? this.currentImg : this.computedImg);
		add(this.imageLabel, "South");
		revalidate();
	}

	public void updateAfterButton(boolean next) {
		if (next && this.currentIndex < parent.getImgs().size() - 1)
			this.currentIndex++;
		if (!next && this.currentIndex > 0)
			this.currentIndex--;
		update(false);
	}

	private JLabel getLabelFromMat(Mat img) {
		return new JLabel(new ImageIcon(Utils.createAwtImage(img)));
	}

	public Mat getCurrentImg() {
		return currentImg;
	}

	public void setCurrentImg(Mat currentImg) {
		this.currentImg = currentImg;
	}
}
