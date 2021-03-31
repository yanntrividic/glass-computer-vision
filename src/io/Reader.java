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
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class Reader {
	
	public static final String fp = File.separator ;
	
	public static String getImgDir(String type) {
		return getResourcesDir()+"img"+fp+type+fp ;
	}
	
	public static String getResourcesDir() {
		return System.getProperty("user.dir")
				+fp+"src"
				+fp+"resources"+fp ;		
	}
	
	public static String getLabelsDir(String type) {
		return getResourcesDir() + "labels" + fp + type + fp;
	}
	
	/**
	 * Extracts JSON data 
	 * @param path Path towards the JSON file
	 */
	public static Mat extractLabelsFromJSON(String path) {
		// FIXME: je pense que la signature de la méthode devrait être Mat plutôt que void
		//System.out.println("Here : " + path );
		try {
			JSONParser parser = new JSONParser();
			//FIXME : problème à la ligne d'en dessous, chemin vers img et pas json...
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
			Mat polygones = new Mat(taille, 16);
			
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
	
	private static Point convertToPoint(String s) {
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
	
//  unused for now	
//	private static ArrayList<String> getFileNamesWithExtension(String path, String extension) {
//		return getFileNamesWithExtension(path, new String[] {extension}) ;
//	}
	
	/**
	 * Gets the list of files of a particular set of extensions
	 * @param path folder where the files are supposed to be
	 * @param extensions array of extensions
	 * @return an arraylist of strings that contains the names of the files found
	 * @exception throws an exception if the folder does not exist
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
}
