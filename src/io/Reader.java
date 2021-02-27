package io;

import java.io.File;

public class Reader {
	public static String getImgDir() {
		return System.getProperty("user.dir")
				+File.separator+"src"
				+File.separator+"resources"
				+File.separator+"img"
				+File.separator ;
	}
	
	/**
	 * Extracts JSON data 
	 * @param path Path towards the JSON file
	 */
	public static void extractLabelsFromJSON(String path) {
		// TODO Qu'est-ce qu'on extrait ? Comment représentet-on les données ? Fait-on une classe à part ?
	}
}
