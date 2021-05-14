package cv;

import java.awt.Color;
//import java.awt.Point;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class EllipseFinder {
	
	/**
	 * * The function that finds the ellipse with the best score. This function calls the methods drawEllipse, getFirstPixel,getLeftPixel,getRightPixel
	 * @param img a picture with a glass on it
	 * @param mask the mask to isolate the glass from the rest of the image
	 * @param resizeWidth the the width of the image resize, must take a value between 550 (above no interest) and 200 (below too small), to increase from 50 to 50
	 * @param incli, 3 possible values: 1 if the glass is from the front or the photo taken at a slight angle; 2 if the glass is moderately inclined; 3 if the is very inclined or seen from above. The start and end values of the ellipse height depend on this.
	 * @return an double list containing the left and right points and the height of the ellipse
	 */
	public static ArrayList<Double> getEllipse(Mat img, Mat mask, int resizeWidth, int incli){  //ArrayList<Point> getEllipse(Mat img,Mat mask
		//merge the mask and the image
		//Core.multiply(img, mask, img);
		//show(img,"avant");
		//show(mask,"apres");
		double coeffy=0;
		double coeffx=0;
		if(img.width()>resizeWidth) {
			coeffx=img.width()-resizeWidth;
			int temp=img.height();
			img=cv.PreProcessing.resizeSpecifiedWidth(img,resizeWidth);
			mask=cv.PreProcessing.resizeSpecifiedWidth(mask,resizeWidth);
			coeffy=temp-img.height();
			System.out.println("coeff x"+coeffx+" coeff y"+coeffy);
		}
		Mat imgComp=img.clone();
		
		int ellHeightStart=10*(resizeWidth/600); 
		int ellHeightEnd=90*(resizeWidth/600);         //car opti � 80 pour resize de 600
		if(incli==2) {
			ellHeightStart=90*(resizeWidth/600);
			ellHeightEnd=160*(resizeWidth/600);
		}
		else if(incli==3) {
			ellHeightStart=160*(resizeWidth/600);
			ellHeightEnd=240*(resizeWidth/600);
		}
		int step=resizeWidth/80;   //400->5    600->
		
		
		img=fusionImgMask(img,mask);
					
		//the starting position for the course
		Point startPos=getFirstPixel(img);
			
		
		//the limit height of the glass surface on the image
		int heightLimit=getHeightLimit(img,startPos);
		heightLimit=heightLimit-((heightLimit/100)*10);//it is very rare that the glass is filled to only 10%, moreover the lower part often contains large differences between its pixels which truncates the result
		
		//the list of points of the ellipse with the best score 
		ArrayList<Point> bestEllipse=new ArrayList<Point>();
		//we create a first ellipse to be able to compare
		bestEllipse=createEllipse(startPos,getRightPixel(img,(int)startPos.x,(int)startPos.y),img,10);
		
		double xleftPointEllipse=startPos.x;
		double yleftPointEllipse=startPos.y;
		double xrightPointEllipse=getRightPixel(img,(int)startPos.x,(int)startPos.y).x;
		double yrightPointEllipse=getRightPixel(img,(int)startPos.x,(int)startPos.y).y;
		double heightEllipse=10;
		////run through the image, increasing the height by 5pixels as you go
		for(int i=(int)startPos.y+step;i<heightLimit;i+=(step*2)) {   //y parameter
			//get the left and right pixels at height i
			Point left=getLeftPixel(img,i);
			Point right=getRightPixel(img,(int)left.x,(int)left.y);
			
			////we create all possible ellipses at this height, z corresponds to its height, it starts at 10 and increases from 10 to 80
			for(int z=ellHeightStart;z<ellHeightEnd;z+=step) { //z parameter
				
				ArrayList<Point> tempEllipse=createEllipse(left,right,img,z);   //drawEllipse(new Point(i,yS),new Point(i,yE),path);
				//the current score is compared with the score of the ellipse 
				if(getEllipseScore(imgComp, tempEllipse)>getEllipseScore(imgComp, bestEllipse)) {
					bestEllipse=tempEllipse;
					xleftPointEllipse=left.x;
					yleftPointEllipse=left.y;
					xrightPointEllipse=right.x;
					yrightPointEllipse=right.y;
					heightEllipse=z;
				}
			}
			
		}
		//show the best Ellipse
		//testDrawn(bestEllipse,img);
		
		ArrayList<Double>resEll=new ArrayList<Double>();
		resEll.add(xleftPointEllipse+coeffx);
		resEll.add(yleftPointEllipse+coeffy);
		resEll.add(xrightPointEllipse+coeffx);
		resEll.add(yrightPointEllipse+coeffy);
		resEll.add(heightEllipse+coeffy);
		return(resEll);
		//return bestEllipse;
	}

	/**
	 * Gets the leftmost and top pixel of the glass
	 * @param img the image where the glass is
	 * @return the top left pixel of the glass
	 */
	public static Point getFirstPixel(Mat img) {
		Point p=new Point(0,0);
		
		for(int i=(img.width()/100)*10;(i<img.width())&&(p.x==0);i++) {                  //on commence pas � 0, mais � 10 % de la taille
			for(int j=(img.height()/100)*30;j<img.height()&&p.x==0;j++) {                  //30%
				if(img.get(j, i)[0]!=0) {
					p=new Point(i,j);
				}
			}
		}
		return p;
	}
	/**
	 * get the height of the glass on an image
	 * @param img the image contening the image and a black background
	 * @param start the point of start of the glass
	 * @return the height of the glass 
	 */
	public static int getHeightLimit(Mat img, Point start) {
		int res=0;
		for(int  i=(int)start.y;i<img.height()-1&&(res==0);i++) {
			if(img.get(i+1,img.width()/2)[0]==0) {
				res=i;
			}
		}
		System.out.println("res"+res);
		return res;
	}
	/**
	 * get the left pixel of the glass on a height
	 * @param img the image where the glass is
	 * @param heigth the level of the glass
	 * @return the leftmost pixel
	 */
	public static Point getLeftPixel(Mat img,int heigth) {
		Point p=new Point(0,0);
		for(int i=0;i<img.width()&&p.x==0;i++) {
			if(img.get(heigth, i)[0]!=0) {
				p=new Point(i,heigth);
			}
		}
		return p;
	}
	/**
	 * get the right pixel of the glass on a height
	 * @param img the image where the glass is
	 * @param start the starting position
	 * @param heigth the level of the glass
	 * @return the pixel of the right border of the height level
	 */
	public static Point getRightPixel(Mat img,int start,int heigth) {
		Point p=new Point(0,0);
		for(int i=img.width()/2;i<img.width()&&p.x==0;i++) {//avant i=start
			
			if((img.get(heigth, i+1)[0]==0)||(i==(img.width()-2))) {
				p=new Point(i,heigth);
			}
		}
		return p;
	}

	/**
	 * create an ellipse, then return the list of its points
	 * @param left The left point, the starting point of the ellipse
	 * @param right The right point, the end point of the ellipse
	 * @param src  The image where the ellipse must be made
	 * @param height the height of the ellipse in the glass
	 * @return a list of points corresponds to the plotted ellipse
	 */
	public static ArrayList<Point> createEllipse(Point left,Point right,Mat src,int height) {
		
	      //Reading the source image in to a Mat object
	      
	    //Reading the source image in to a Mat object
	      //Mat src = Imgcodecs.imread(path);
	      //Drawing an Ellipse
	      //src=resize(src);
		  Mat temp = new Mat();
		  temp= src.clone();//The image is cloned each time so that the ellipses are not made directly on the image, as they would then overlap
		 
	      Size sz=new Size(right.x-left.x,height);  //the size of the future box
	     
	      Point center=new Point((left.x+right.x)/2,left.y);//the centre of the ellipse 
	      
	      RotatedRect box = new RotatedRect(center,sz,0);//the last parameter is the tilt angle, and MAY BE A PARAMETER 
	      Scalar color = new Scalar(255, 255, 255); //the colour of the curve, here it is white MAY BE A PARAMETER 
	      int thickness = 1;
	      Imgproc.ellipse (temp, box, color, thickness);  //the ellipse is drawn
	
	      //Imgproc.ellipse(src, new Point(150,150), new Size(260, 180), 25.0, 25.0, 25.0, color); test autres arguments
	      
	      //HighGui.imshow("Drawing an ellipse"+right, temp);//displays the ellipsis, can be deleted
	     // HighGui.waitKey(10);//the value 10 allows ellipses to be displayed in a consistent manner
	      
	   return getEllipseDraw(temp,Color.white,(int)right.y,height);
	      
	   }
	
	/**
	 * draw an ellipse
	 * @param left The left point, the starting point of the ellipse
	 * @param right The right point, the end point of the ellipse
	 * @param src The image where the ellipse must be made
	 * @param height the height of the ellipse in the glass
	 * @return the image with the ellipse
	 */
	public static Mat drawEllipse(Point left,Point right,Mat src,double height) {
		Mat res=new Mat();
		res= src.clone();
	      Size sz=new Size(right.x-left.x,height);  //the size of the future box
	     
	      Point center=new Point((left.x+right.x)/2,left.y);//the centre of the ellipse 
	      
	      RotatedRect box = new RotatedRect(center,sz,0);
	      Scalar color = new Scalar(255, 255, 255); 
	      int thickness = 2;
	      Imgproc.ellipse (res, box, color, thickness);  
	
	      
	   return res;
	      
	   }
	
	
	
	/**
	 * 
	 allows to obtain the ellipse drawn previously
	 * @param img picture where the ellipse is
	 * @param col the color of the ellipse
	 * @param height the height of the start on the image
	 * @param heightEll the height of the ellipse
	  * @return the list of points that make up the ellipse
	 */
	public static ArrayList<Point> getEllipseDraw(Mat img,Color col,int height,int heightEll) {//maybe a greyscale problem
		ArrayList<Point> ellipse=new ArrayList<Point>();
		for(int i=height-(heightEll)/2;i<img.height()-(heightEll)/2;i++) {
			for(int j=0;j<img.width();j++) {
				//System.out.println(""+i+" "+j);
				double[]temp=img.get(i, j);
				//for(int k=0;k<temp.length;k++) {
				//System.out.println(temp[0]+" "+temp[1]+" "+temp[2]);
					if(temp[0]==255) {//&&temp[1]==255&&temp[2]==255)
						ellipse.add(new Point(i,j));
				}
			}
		}
		return ellipse;
	}
	/**
	 * Calculates the score of an ellipse using Sagi Eppel's method, "Point by point evaluation of the curve environment"
	 * @param img the image where the ellipse should be
	 * @param ellipse the ellipse as a list of points
	 * @return the ellipse's score
	 */
	public static double getEllipseScore(Mat img, ArrayList<Point> ellipse) {
		//U ->points above the ellipse
		//D ->points below the ellipse
		double maxU=0;
		double maxD=0;
		double sommU=0; 
		double sommD=0;
		//mal plac�
		//Mat temp=new Mat();
		//grayscale before calculating the score
		//Imgproc.cvtColor(img, temp, Imgproc.COLOR_RGB2GRAY);
		
		for(int i=0;i<ellipse.size();i++) {
			sommU+=img.get((int)ellipse.get(i).x-1, (int)ellipse.get(i).y)[0];    //image en noir et blanc 
			if(img.get((int)ellipse.get(i).x-1, (int)ellipse.get(i).y)[0]>maxU)
				maxU=img.get((int)ellipse.get(i).x-1,(int) ellipse.get(i).y)[0];
			//System.out.println("test"+img.get((int)ellipse.get(i).x+1,(int) ellipse.get(i).y)[0]);
			sommD+=img.get((int)ellipse.get(i).x+1,(int) ellipse.get(i).y)[0];
			if(img.get((int)ellipse.get(i).x-1,(int) ellipse.get(i).y)[0]>maxU)
				maxD=img.get((int)ellipse.get(i).x+1,(int)ellipse.get(i).y)[0];
		}
		double meanU=sommU/ellipse.size();
		double meanD=sommD/ellipse.size();
		double max=0;
		if(maxU>maxD)
			max=maxU;
		else
			max=maxD;	
		return((meanU-meanD)/max);
		
	}

/**
 * merges an image with a black and white mask
 * @param img the image
 * @param mask the mask in white and black
 * @return the merge image
 */
public static Mat fusionImgMask(Mat img,Mat mask) {
		for(int i=0;i<mask.height();i++) {
			for(int j=0;j<mask.width();j++) {
				if(mask.get(i, j)[0]==0) {   //because of grayscale
					double[]temp= {0,0,0};
					img.put(i, j, temp);
				}
			}
		}
		return img;
	}
	
	//not use
/*
public static Mat fusion(Mat img,Mat mask) {
	//cv.Segmentation.simpleBinarization(mask, 1, false);
	
	System.out.println("mask type"+mask.type());
	System.out.println("img type"+img.type());
	Mat n=new Mat();
	Core.multiply(img, mask, n);

	return img;
	
}
*/


}