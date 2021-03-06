package io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Class to handle file inputs
 * @author Yann Trividic
 * @version 1.0
 */
public class Reader {
	
	public static final String fp = File.separator ;
	
	/**
	 * Returns one of the images' directory
	 * @param type String object that can be "train", "test" or "validation
	 * @return a String path
	 * @author Yann Trividic
	 */
	public static String getImgDir(String type) {
		return getResourcesDir()+type+fp ;
	}
	
	/**
	 * Method that fecthes the resources directory as a path
	 * @return the img directory of the project as a String
	 * @author Yann Trividic
	 */
	public static String getResourcesDir() {
		return System.getProperty("user.dir")
				+fp+"img"+fp ;		
	}
	
	/**
	 * Method that fecthes the path of the gaussian mask
	 * @return the path of the gaussian mask
	 * @author Yann Trividic
	 */
	public static String getGaussianMaskPath() {
		return getResourcesDir()+fp+"gaussian_distribution.jpg" ;		
	}
	
	/**
	 * Returns one of the labels' directory
	 * @param type String object that can be "train", "test" or "validation
	 * @return a String path
	 * @author Yann Trividic
	 */
	public static String getLabelsDir(String type) {
		return getResourcesDir() + "labels" + fp + type + fp;
	}
	
	/**
	 * Extracts JSON data 
	 * @param path Path towards the JSON file
	 * @author Erwan Lacoudre
	 * @return a Mat object that contains the extracted labels
	 */
	public static Mat extractLabelsFromJSON(String path) {
		//System.out.println("Here : " + path );
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(path));
			JSONObject JSONobj = (JSONObject) obj;
			
			
			int[] taille = {
					Integer.parseInt(JSONobj.get("imageHeight").toString()),
					Integer.parseInt(JSONobj.get("imageWidth").toString())
					};
			
			//ça change quoi le type de la matrice ?
			// FIXME: c'est avec le type que tu spécifies si ton image est RGB, GrayScale, HSV ou autre, tu peux aussi
			// préciser le nombre de bits utilisés pour chaque canal. Par exemple, CvType.CV_8UC1 est le type 
			// correspondant à 8 bits sur 1 canal (de 0 à 255 sur un seul canal, du GrayScale donc.)
			Mat polygones = new Mat(taille, CvType.CV_8UC3, new Scalar(0, 0, 0));
			
			JSONArray shapes = (JSONArray) JSONobj.get("shapes");
			
			for(Object o : shapes) {
				JSONObject jsonObject = (JSONObject) o;
				JSONArray points = (JSONArray) jsonObject.get("points");
				
				
				for(int i = 0; i < points.size() - 1; i++) {
					String dep = points.get(i).toString();
					String arr = points.get(i+1).toString();
					//System.out.println("From : " + dep + "To : " + arr);
					Imgproc.line(polygones, convertToPoint(dep) , convertToPoint(arr), new Scalar (0,0, 255));
				}
				
				Imgproc.line(polygones, convertToPoint(points.get(0).toString()), 
						convertToPoint(points.get(points.size() - 1).toString()), new Scalar(0,0,255));
				
			}
			return polygones;
		} catch(IOException e) {
			System.out.println(e.getMessage());
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	/**
	 * This methods converts to Point type from a String 
	 * @param s String containing the coordinates
	 * @return the coordinates in a Point
	 * @author Erwan Lacoudre
	 */
	public static Point convertToPoint(String s) {
		Point p = new Point();
		
		String[] str = s.substring(1, s.length()-1).split(",");
		
		double[] point = {
				Double.parseDouble(str[0]), Double.parseDouble(str[1])
		};
		
		p.set(point);
		return p;
	}
	
	public static ArrayList<String> getAllImgInFolder(String path) {
		return getFileNamesWithExtension(path, new String[]{"png", "jpg", "jpeg"}) ;
	}
	
	public static ArrayList<String> getAllLabelsInFolder(String path) {
		return getFileNamesWithExtension(path, new String[]{"json"}) ;
	}
	
	/**
	 * Gets the list of files of a particular set of extensions
	 * @param path folder where the files are supposed to be
	 * @param extensions array of extensions
	 * @return an arraylist of strings that contains the names of the files found
	 * @exception throws an exception if the folder does not exist
	 * @author Yann Trividic
	 */
	private static ArrayList<String> getFileNamesWithExtension(String path, String [] extensions) {
		File f = new File(path) ;
		String [] filesList = f.list() ; // list all the files in this folder
		
		ArrayList<String> files = new ArrayList<String>() ; // for each file, we check if the extension matches
		for(int i = 0 ; i < filesList.length ; i++)	{
			if(!new File(path+fp+filesList[i]).isDirectory()) {
				for(String ext : extensions) if(filesList[i].toLowerCase().endsWith("."+ext)) files.add(filesList[i]) ; 
			}
		}
		
		System.out.println("We found "+files.size()+((extensions.length == 1)?" "
							+ extensions[0].toUpperCase():"")+" files in "+path) ;
		
		Collections.sort(files) ; // alphabetical order
 		return files ;
	}
	
	
	/**
	 * Get the labels filled and separated in two
	 * Help from : https://stackoverflow.com/questions/48421541/how-to-draw-a-fill-shape-in-opencv
	 * @param path path to the JSON file
	 * @return an array, the first element is the ellipse filled and the second the glass filled
	 * @author Erwan
	 */
	public static Mat[] getFilledLabels(String path) {
		Mat[] labels = new Mat[2];
		
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(path));
			JSONObject JSONobj = (JSONObject) obj;
			
			
			int[] taille = {
					Integer.parseInt(JSONobj.get("imageHeight").toString()),
					Integer.parseInt(JSONobj.get("imageWidth").toString())
					};
			
			//Mat polygones = new Mat(taille, CvType.CV_8UC3, new Scalar(0, 0, 0));
			
			JSONArray shapes = (JSONArray) JSONobj.get("shapes");
			for(Object o : shapes) {
				JSONObject jsonObject = (JSONObject) o;
				JSONArray points = (JSONArray) jsonObject.get("points");
				
				Mat img = new Mat(taille, CvType.CV_8UC1, new Scalar(0, 0, 0));
				ArrayList<Point> pts = new ArrayList<Point>();
				for(int i = 0; i < points.size(); i++) {
					pts.add(convertToPoint(points.get(i).toString()));
				}
				
				MatOfPoint matPt = new MatOfPoint();
		        matPt.fromList(pts);
		        
		        ArrayList<MatOfPoint> ppt = new ArrayList<MatOfPoint>();
		        ppt.add(matPt);
	
		        Imgproc.fillPoly(img,
		                ppt,
		                new Scalar(255,255,255));
		        if(jsonObject.get("label").toString().equals("ellipse"))
		        	labels[0] = img;
		        else 
		        	labels[1] = img;
			}
			
		
			return labels; 
		} catch(IOException e) {
			System.out.println(e.getMessage());
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	
	/**
	 * Only for testing
	 * @param args
	 */
	/*
	public static void main(String[] args) {
		nu.pattern.OpenCV.loadLocally(); // loads opencv for this run
		Mat img = extractLabelsFromJSON("/Users/erwan/Documents/Licence/L3/S6/image/glass-computer-vision/img/train/0.json");
		
		HighGui.imshow("test", img);
		HighGui.waitKey();
		
		Mat[] filled = getFilledLabels("/Users/erwan/Documents/Licence/L3/S6/image/glass-computer-vision/img/train/0.json");
		for(int i = 0; i < 2; i++) {
			HighGui.imshow("test", filled[i]);
			HighGui.waitKey();
		}
	}
	*/
	
}
