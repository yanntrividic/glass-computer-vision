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
	 * The function that finds the ellipse with the best score. This function calls the methods drawEllipse, getFirstPixel,getLeftPixel,getRightPixel
	 * @param img a picture with a glass on it
	 * @param mask the mask to isolate the glass from the rest of the image
	 * @return
	 */
	public static ArrayList<Double> getEllipse(Mat img, Mat mask,int resizewidth){  //ArrayList<Point> getEllipse(Mat img,Mat mask
		//merge the mask and the image
		//Core.multiply(img, mask, img);
		//show(img,"avant");
		//show(mask,"apres");
		double coeff=img.width()/resizewidth;
		img=cv.PreProcessing.resizeSpecifiedWidth(img,resizewidth);
		mask=cv.PreProcessing.resizeSpecifiedWidth(mask,resizewidth);
		Mat imgComp=cv.PreProcessing.resizeSpecifiedWidth(img,resizewidth);
		img=fusionImgMask(img,mask);
			
		
		
		//merge the mask and the image
		//img=cv.Segmentation.simpleBinarization(mask, 1, false);
		//Core.multiply(img, mask, img);
		//show(img,"test1");
		
		
		//we resize the image to limit the number of operations
		//img=cv.PreProcessing.resizeSpecifiedWidth(img,500); //PARAMETER
		
		
		//the starting position for the course
		Point startPos=getFirstPixel(img);
			
		//startPos.x=startPos.x+(img.height()*10)/100;  //� verifier
		Point endPos=new Point();
		
		//the end position, it corresponds to 2/3 of the image, this value is arbitrary and must be changed
		endPos.y=(img.height()/100)*80; //� revoir
		
		
		//the list of points of the ellipse with the best score 
		ArrayList<Point> bestEllipse=new ArrayList<Point>();
		//we create a first ellipse to be able to compare
		bestEllipse=drawEllipse(startPos,getRightPixel(img,(int)startPos.x,(int)startPos.y),img,10);
		
		double xleftPointEllipse=startPos.x;
		double yleftPointEllipse=startPos.y;
		double xrightPointEllipse=getRightPixel(img,(int)startPos.x,(int)startPos.y).x;
		double yrightPointEllipse=getRightPixel(img,(int)startPos.x,(int)startPos.y).y;
		double heightEllipse=10;
		////run through the image, increasing the height by 5pixels as you go
		for(int i=(int)startPos.y+5;i<endPos.y;i+=5) {   //y parameter
			//get the left and right pixels at height i
			Point left=getLeftPixel(img,i);
			Point right=getRightPixel(img,(int)left.x,(int)left.y);
			
			////we create all possible ellipses at this height, z corresponds to its height, it starts at 10 and increases from 10 to 80
			for(int z=10;z<80;z+=5) { //z parameter
				
				ArrayList<Point> tempEllipse=drawEllipse(left,right,img,z);   //drawEllipse(new Point(i,yS),new Point(i,yE),path);
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
		resEll.add(xleftPointEllipse*coeff);
		resEll.add(yleftPointEllipse*coeff);
		resEll.add(xrightPointEllipse*coeff);
		resEll.add(yrightPointEllipse*coeff);
		resEll.add(heightEllipse*coeff);
		return(resEll);
		//return bestEllipse;
	}
	/**
	 * allows you to load an image
	 * @param path the path of the image 
	 * @return the loaded image
	 */
	public static Mat loadPicture(String path) {
		nu.pattern.OpenCV.loadLocally();
		Imgcodecs imageCodecs =new Imgcodecs();
		Mat matrix=imageCodecs.imread(path);
		return matrix;
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
	 * 
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
	 * 
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
	/*
	public static Point getLastPixel(Mat img,int start) {
		Point p=new Point(0,0);
		for(int i=start;i<img.width()||p.x!=0;i++) {
			for(int j=0;j<img.height()||p.x!=0;j++) {
				if(img.get(i+1, j)[0]==0) {
					p=new Point(i,j);
				}
			}
		}
		return p;
	}
	*/
	/**
	 * 
	 * @param left The left point, the starting point of the ellipse
	 * @param right The right point, the end point of the ellipse
	 * @param src  The image where the ellipse must be made
	 * @param height the height of the ellipse in the glass
	 * @return a list of points corresponds to the plotted ellipse
	 */
	public static ArrayList<Point> drawEllipse(Point left,Point right,Mat src,int height) {
		
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
	      
	   return getEllipseDraw(temp,Color.white,(int)right.y);
	      
	   }
	
	public static Mat testDrawn(ArrayList<Point>list,Mat img)
	{
		double[] col= {212.0, 43.0, 48.0};
		for(int i=0;i<list.size();i++) {
			
			img.put((int)list.get(i).x,(int)list.get(i).y,col);
		}
		
		//show(img,"testDrawn");
		return img;
	}
	
	/**
	 * For testing purposes only
	 * @param matrix
	 * @param legend
	 */
	public static void show(Mat matrix,String legend) {
		HighGui.imshow(legend, matrix);
		HighGui.waitKey(0);
		//System.exit(0);
		
	}
	
	

	
	/**
	  allows to obtain the ellipse drawn previously
	 * @param img picture where the ellipse is
	 * @param col the color of the ellipse
	 * @return the list of points that make up the ellipse
	 */
	public static ArrayList<Point> getEllipseDraw(Mat img,Color col,int height) {//maybe a greyscale problem
		ArrayList<Point> ellipse=new ArrayList<Point>();
		for(int i=height-80;i<img.height();i++) {
			for(int j=0;j<img.width();j++) {
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
	 * allows you to resize the image to avoid too many operations
	 * @param img the image to resize
	 * @return the image in the new format(if necessary)
	 */
	public static Mat resize(Mat img) {//permet aussi d'eviter les trop longs de chargement
		Mat res=new Mat();
		Size sz=new Size();
		if(img.height()>=2000) {
			sz=new Size(img.width()/5,img.height()/5);
			Imgproc.resize( img, res, sz);
		}
		else if(img.height()>=1600) {
			sz=new Size(img.width()/3,img.height()/3);
			Imgproc.resize( img, res, sz);
		}
		else if(img.height()>=1000) {
			sz=new Size(img.width()/1.5,img.height()/1.5);
			Imgproc.resize( img, res, sz);
		}
		return res;
	}
	
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
public static Mat fusion(Mat img,Mat mask) {
	//cv.Segmentation.simpleBinarization(mask, 1, false);
	
	System.out.println("mask type"+mask.type());
	System.out.println("img type"+img.type());
	Mat n=new Mat();
	Core.multiply(img, mask, n);

	return img;
	
}
public static void main(String[]args) {
	Mat img=loadPicture("E:\\image\\18img.png");
	
	Mat mask=loadPicture("E:\\image\\18masque.png");
	//Imgproc.cvtColor(img2, img2, Imgproc.COLOR_BGR2GRAY);
	
	getEllipse(img,mask,400);
}

}