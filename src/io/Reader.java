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
}
