package io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Reader {
	
	public static final String fp = File.separator ;
	
	public static String getImgDir() {
		return getResourcesDir()+"img"+fp ;
	}
	
	public static String getResourcesDir() {
		return System.getProperty("user.dir")
				+fp+"src"
				+fp+"resources"+fp ;		
	}
	
	/**
	 * Extracts JSON data 
	 * @param path Path towards the JSON file
	 */
	public static void extractLabelsFromJSON(String path) {
		// TODO Qu'est-ce qu'on extrait ? Comment représentet-on les données ? Fait-on une classe à part ?
	}
	
	public static ArrayList<String> getAllImgInFolder(String path) {
		return getFileNamesWithExtension(path, new String[]{"png", "jpg", "jpeg"}) ;
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
				for(String ext : extensions) if(filesList[i].endsWith("."+ext)) files.add(filesList[i]) ; 
			}
		}
		
		System.out.println("We found "+files.size()+((extensions.length == 1)?" "
							+ extensions[0].toUpperCase():"")+" files in "+path+"\n") ;
		
		Collections.sort(files) ; // alphabetical order
 		return files ;
	}
}
