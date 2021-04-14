package io;

import org.json.simple.JSONArray;
import org.opencv.core.Point;

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
			dist0 = euclideanDistance(farthestPoints[0], Reader.convertToPoint(form.get(i).toString()));
			//Distance between current point and the second point
			dist1 = euclideanDistance(Reader.convertToPoint(form.get(i).toString()), farthestPoints[1]);
			
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
	public static double[] middleSegment(Point[] segment) {
		double midX = (segment[0].x + segment[1].x)/2;
		double midY = (segment[0].y + segment[1].y)/2;
		
		return new double[] {midX, midY};
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
		 * and at the vertical of the center of the ellipse such as we obtain a right triangle 
		 */
		Point projection = new Point(ellipseCenter.x, ellipseLowest.y);
		
		return Math.acos(Math.toRadians(
						euclideanDistance(projection, ellipseLowest) 
						/ euclideanDistance(ellipseCenter, ellipseLowest))
						);

	}
}
