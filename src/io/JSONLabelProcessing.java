/**
 * Class for the computation of the JSON obtained from the Reader class
 * @author Erwan Lacoudre
 * @version 1.0
 */

package io;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
//import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

public class JSONLabelProcessing {
	/**
	 * Get the the farthest points of the form
	 * @param form points of the form
	 * @return the two farthest points of the form
	 */
	public static Point[] farthestPoints(JSONArray form) {
		Point[] farthestPoints = {Reader.convertToPoint(form.get(0).toString()), 
				Reader.convertToPoint(form.get(1).toString())
		};
		
		double maxDist = euclideanDistance(farthestPoints[0], farthestPoints[1]);
		double dist0, dist1;
		
		for(int i = 2; i < form.size(); i++) {
			//Distance between current point and the first point
			dist0 = euclideanDistance(farthestPoints[0], 
					Reader.convertToPoint(form.get(i).toString()));
			//Distance between current point and the second point
			dist1 = euclideanDistance(Reader.convertToPoint(form.get(i).toString()), 
					farthestPoints[1]);
			
			//If true, then these are the two farthest points so far
			//(First element of farthestPoints and the current point)
			/// => the second point is closer and needs to be changed
			if(dist0 > maxDist) {
				maxDist = dist0;
				farthestPoints[1] = Reader.convertToPoint(form.get(i).toString());
			}
			
			//Equivalent of above, but for the second element of farthestPoints
			else if(dist1 > maxDist) {
				maxDist = dist1;
				farthestPoints[0] = Reader.convertToPoint(form.get(i).toString());
			}
		}
		return farthestPoints;
	}
	
	/**
	 * Finds the point at the middle of the segment
	 * @param segment the two points of the segment
	 * @return middle point of the segment
	 */
	public static Point middleSegment(Point[] segment) {
		double midX = (segment[0].x + segment[1].x)/2;
		double midY = (segment[0].y + segment[1].y)/2;
		
		return new Point(midX, midY);
	}
	
	
	/**
	 * Calculates the euclidean distance between two points
	 * @param a first point
	 * @param b second point
	 * @return the euclidean distance
	 */
	public static double euclideanDistance(Point a, Point b) {
		return Math.sqrt(
				Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2)
				);
	}

	
	/**
	 * Finds the angle between the horizontal and the segment between the two points
	 * @param ellipseCenter the center of the ellipse
	 * @param ellipseLowest the lowest of the two farthest points of the ellipse
	 * @return the angle between the horizon 
	 */
	public static double horizontalAngle(Point ellipseCenter, Point ellipseLowest) {
		/*
		 * The point is placed on the horizontal line passing through ellipseLowest
		 * and at the vertical of the center of the ellipse such as we obtain 
		 * a right triangle 
		 */
		Point projection = new Point(ellipseCenter.x, ellipseLowest.y);
		
		//double tetha = euclideanDistance(projection, ellipseLowest) 
		//		/ euclideanDistance(ellipseCenter, ellipseLowest); 
		
		//System.out.println("cosine  = " + tetha);
		return Math.acos(
						euclideanDistance(projection, ellipseLowest) 
						/ euclideanDistance(ellipseCenter, ellipseLowest)
						);

	}
	
	
	/**
	 * Creates a Mat containing the line at the center of the ellipse with the 
	 * correct angle
	 * @param start one point of the line
	 * @param angle the angle (in degrees) compared to the horizontal
	 * @param addAngle boolean if we should add the angle or not compared to 
	 * the vertical 
	 * @param height the height of the original picture
	 * @param width the width of the original picture
	 * @return
	 */
	public static Mat drawLineAngle(Point start, double angle, boolean addAngle, 
									int height, int width) {
		Mat mat = new Mat(height, width, CvType.CV_8UC3);
		Point pTop = new Point(), pBottom = new Point();
		if(addAngle) {
			pTop.x = (angle == 0)? start.x: start.x - (start.y * Math.tan(angle));
			pTop.y = 0;
			
			pBottom.x = (angle == 0)? start.x: start.x + (height - start.y) * Math.tan(angle);
			pBottom.y = height;
		} else {
			pTop.x = (angle == 0)? start.x: start.x + (start.y * Math.tan(angle));
			pTop.y = 0;
			
			pBottom.x = (angle == 0)? start.x: start.x - (height - start.y) * Math.tan(angle);
			pBottom.y = height;
		}
		
		//System.out.println("Points : " + pTop + "\t" + pBottom);
		Imgproc.line(mat, new Point(0, start.y), new Point(width, start.y), 
				new Scalar(255, 0, 0), 2);
		Imgproc.line(mat, new Point(start.x, 0), new Point(start.x, height), 
				new Scalar(255,0,0), 2);
		Imgproc.line(mat, start, pTop, new Scalar(0,0,255), 2);
		Imgproc.line(mat, start, pBottom, new Scalar(0,0,255), 2);
		
		//HighGui.imshow("DrawLine", cv.PreProcessing.resizeSpecifiedWidth(mat, 
		//		(int) (mat.cols() * 0.3)));
		//HighGui.waitKey();
		
		return mat;
	}
	
	/**
	 * Computes the filling level of the glass
	 * @param pathToJSON path to the file containing the labels
	 * @return the filling level (in percentages)
	 */
	public static double liquidLevel(String pathToJSON) {
		Mat mat = Reader.extractLabelsFromJSON(pathToJSON);
		//HighGui.imshow("Labels from JSON", cv.PreProcessing.resizeSpecifiedWidth(mat, 
		//		(int) (mat.cols() * 0.3)));
		//HighGui.waitKey();
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(pathToJSON));
			JSONObject JSONobj = (JSONObject) obj;
			JSONArray shapes = (JSONArray) JSONobj.get("shapes");
			
			//int height = Integer.parseInt(JSONobj.get("imageHeight").toString());
			//int width = Integer.parseInt(JSONobj.get("imageWidth").toString());
			
			
			
			for(Object o : shapes) {
				JSONObject jsonObject = (JSONObject) o;
				JSONArray points = (JSONArray) jsonObject.get("points");
				
				//if ellipse then do the following
				if(jsonObject.get("label").equals("ellipse")) {
					Point[] farthests = farthestPoints(points);
					System.out.println("Resultat : (" + farthests[0] + ',' + 
										farthests[1] + ')');
					//Point lowest = (farthests[0].y < farthests[1].y)? farthests[0]: farthests[1];
					
					Point lowest = new Point();
					boolean addAngle;
					
					if((farthests[0].y > farthests[1].y)) {
						lowest = farthests[0];
						addAngle = true;
					} else {
						lowest = farthests[1];
						addAngle = false;
					}
					
					Point middle = middleSegment(farthests);
					System.out.println("middle : " + middle);
					
					double angle = horizontalAngle(middle, lowest);
					System.out.println("Angle value : " + angle);
					
					//System.out.println("\n mat.size().height : " + mat.size().height);
					Mat lineDrawn = drawLineAngle(middle, angle, addAngle, 
									(int)mat.size().height, (int)mat.size().width);
					
					//System.out.println("mat size : " + mat.size() + "// line size : " + lineDrawn.size());
					Mat mult = mat.mul(lineDrawn);
					
					Point topGlass = new Point(), bottomGlass = new Point(), bottomEllipse = new Point();
					int numberPointsFound = 0;
					for(int i = 0; i < mult.size().height; i++) {
						for(int j = 0; j < mult.size().width; j++) {
							if(mult.get(i, j)[2] != 0) {
								//System.out.println("value : " + mult.get(i, j)[2]);
								//System.out.println("coords : (" + i + ',' + j + ')');
								numberPointsFound++;
								//First point encountered : the top of the glass
								if(numberPointsFound == 1) {
									topGlass.x = j;
									topGlass.y = i;
								}
								//Second and third points : the ellipse of the liquid
								else if(numberPointsFound == 3 ) {
									bottomEllipse.x = j;
									bottomEllipse.y = i;
								}
								//Fourth point is the bottom of the glass
								else if(numberPointsFound == 4) {
									bottomGlass.x = j;
									bottomGlass.y = i;
									//When we've found the 4th point, we can stop the "for" loops
									i = (int) mult.size().height;
									j = (int) mult.size().width;
								}
								//When we've found one point, we can skip a few elements
								i+= 10; j+=10; 
							}
						}
					}
					//System.out.println("NumberPointsFound : " + numberPointsFound);
					//System.out.println(topGlass + "\t" + bottomGlass + "\t" + middle);

					//double heightLiquid = euclideanDistance(middle, bottomGlass);
					double heightLiquid = euclideanDistance(bottomEllipse, bottomGlass);
					double heightGlass = euclideanDistance(bottomGlass, topGlass);
					heightGlass = (heightGlass == 0)? 1: heightGlass;
					
					//System.out.println("glass : " + heightGlass + "\t liquid : " + heightLiquid);
					
					//System.out.println("Filling level : " + (heightLiquid/heightGlass)*100);
					
					
					//HighGui.imshow("Points used", cv.PreProcessing.resizeSpecifiedWidth(mult, 
					//		(int) (mult.cols() * 0.3)));
					//HighGui.waitKey();
					
					return (heightLiquid/heightGlass)*100;
				}
				
			}
			
		}
		catch(IOException e) {
			System.out.println("IO Exception");
		} catch (ParseException e) {
			System.out.println("Parse Exception");
		}
		return 0;
	}
}
