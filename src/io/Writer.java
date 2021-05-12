package io;

import java.io.File;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Class to handle file outputs
 * @author Yann Trividic
 * @version 1.0
 */
public class Writer {
	/**
	 * Saves a Mat object into a JPG file
	 * @param src Mat to save
	 * @param path String relative or absolute filepath with the name of the file to save
	 */
	public static void matToJPG(Mat src, String path) {
		if(!path.toLowerCase().endsWith(".jpeg") || !path.toLowerCase().endsWith(".jpg")) path+=".jpg" ;
		
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			} 
			Imgcodecs.imwrite(path, src);
			System.out.println("Your file \""+path+"\" has been correctly saved.") ;
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
