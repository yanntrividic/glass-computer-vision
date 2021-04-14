import java.io.IOException;
import java.util.ArrayList;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import cv.Extractor;
import cv.PreProcessing;
import io.*;
import ui.Window;

/**
 * Main class of the program
 * 
 * @author Yann Trividic
 * @version 1.0
 */

public class Main {

	public static void main(String[] args) throws Exception {
		nu.pattern.OpenCV.loadLocally(); // loads opencv for this run
		// Apply a look'n feel

		// UIManager.setLookAndFeel( new NimbusLookAndFeel() );

		// Start my window
		Window myWindow = new Window();
		myWindow.setVisible(true);
	}
}