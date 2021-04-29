package cv;


import java.awt.Color;
//import java.awt.Point;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class GestionC4 {
	
	
	public static ArrayList<Point> getEllipse(Mat img,String path){
		ArrayList<Point> ellipse=new ArrayList<Point>();
		Point startPos=getFirstPixel(img);
		System.out.println("pos dep"+startPos);
		
		//startPos.x=startPos.x+(img.height()*10)/100;  //à verifier
		Point endPos=new Point();
		
		endPos.y=(img.height()/3)*2; //à revoir
		System.out.println("fin"+endPos);
		
		double bestScore;
		ArrayList<Point> bestEllipse=new ArrayList<Point>();
		bestEllipse=drawEllipse(startPos,getRightPixel(img,(int)startPos.x,(int)startPos.y),path,10);
		for(int i=(int)startPos.y+5;i<endPos.y;i+=5) {//changer valeur ++
			System.out.println("i="+i);
			Point left=getLeftPixel(img,i);
			Point right=getRightPixel(img,(int)left.x,(int)left.y);//
			//droite=getRightPixel(img,(int)gauche.x,(int) gauche.y)
			System.out.println("gauche "+left+"droite "+right);
			for(int z=10;z<80;z+=5) {
				System.out.println("z="+z);
				ArrayList<Point> tempEllipse=drawEllipse(left,right,path,z);   //drawEllipse(new Point(i,yS),new Point(i,yE),path);
			
				Color col=new Color(255,255,255);
				//ArrayList<Point> tempEllipse=getEllipseDraw(img,col);
				Mat temp=img;
				//Imgproc.cvtColor(temp, img, Imgproc.COLOR_RGB2GRAY);
				if(getEllipseScore(img, tempEllipse)>getEllipseScore(img, bestEllipse)) {
					bestEllipse=tempEllipse;
				}
			}
			
		}
		System.out.println("gg"+bestEllipse);
		testDrawn(bestEllipse,img);
		
		return ellipse;
	}
	/**
	 * 
	 * @param path
	 * @return
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
		System.out.println("imax"+img.width()+" jmax"+img.height());
		for(int i=0;(i<img.width())&&(p.x==0);i++) {
			System.out.println("test");
			for(int j=0;j<img.height()&&p.x==0;j++) {
				//System.out.println("j"+j+" i"+i);
				//System.out.println("ff"+img.get(j, i)[0]);
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
		for(int i=start;i<img.width()&&p.x==0;i++) {
			if(img.get(heigth, i+1)[0]==0) {
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
	public static ArrayList<Point> drawEllipse(Point left,Point right,String path,int height) {
		
	      //Reading the source image in to a Mat object
	      Mat src = loadPicture(path);
	    //Reading the source image in to a Mat object
	      //Mat src = Imgcodecs.imread(path);
	      //Drawing an Ellipse
	      src=resize(src);
	      Size sz=new Size(right.x-left.x,height);  //par défaut 50
	      System.out.println("taille"+sz);
	      Point center=new Point((left.x+right.x)/2,left.y);
	      System.out.println("centre"+center);
	      RotatedRect box = new RotatedRect(center,sz,0);//le dernier parametre correspond à l'angle d'inclinaison
	      Scalar color = new Scalar(255, 255, 255);
	      int thickness = 1;
	      Imgproc.ellipse (src, box, color, thickness);
	      System.out.println("ellipse réalisé");
	      //Imgproc.ellipse(src, new Point(150,150), new Size(260, 180), 25.0, 25.0, 25.0, color); test autres arguments
	      //Saving and displaying the image
	      Imgcodecs.imwrite("E:\\image\\arrowed_line.jpg", src);
	     HighGui.imshow("Drawing an ellipse"+right, src);
	      HighGui.waitKey(10);//changer la valeur de 10
	      
	   return getEllipseDraw(src,Color.white);
	      
	   }
	public static void testDrawn(ArrayList<Point>list,Mat img)
	{
		double[] col= {212.0, 43.0, 48.0};
		for(int i=0;i<list.size();i++) {
			
			img.put((int)list.get(i).x,(int)list.get(i).y,col);
		}
		System.out.println("attention les yeux");
		show(img,"testDrawn");
	}
	public static void show(Mat matrix,String legend) {
		System.out.println("welcome to hell");
		HighGui.imshow(legend, matrix);
		HighGui.waitKey(0);
		//System.exit(0);
		
	}
	
	

	public static void getTheEllipse(Mat img) {
		
	}
	/**
	  allows to obtain the ellipse drawn previously
	 * @param img picture where the ellipse is
	 * @param col the color of the ellipse
	 * @return the list of points that make up the ellipse
	 */
	public static ArrayList<Point> getEllipseDraw(Mat img,Color col) {
		ArrayList<Point> ellipse=new ArrayList<Point>();
		for(int i=0;i<img.height();i++) {
			for(int j=0;j<img.width();j++) {
				double[]temp=img.get(i, j);
				//for(int k=0;k<temp.length;k++) {
				//System.out.println(temp[0]+" "+temp[1]+" "+temp[2]);
					if(temp[0]==255&&temp[1]==255&&temp[2]==255) {
						ellipse.add(new Point(i,j));
				}
			}
		}
		return ellipse;
	}
	
	public static double getEllipseScore(Mat img, ArrayList<Point> ellipse) {
		//U ->points above the ellipse
		//D ->points below the ellipse
		double maxU=0;
		double maxD=0;
		double sommU=0; 
		double sommD=0;
		//mal placé
		Mat temp=new Mat();
		Imgproc.cvtColor(img, temp, Imgproc.COLOR_RGB2GRAY);
		
		for(int i=0;i<ellipse.size();i++) {
			sommU+=temp.get((int)ellipse.get(i).x-1, (int)ellipse.get(i).y)[0];    //image en noir et blanc 
			if(temp.get((int)ellipse.get(i).x-1, (int)ellipse.get(i).y)[0]>maxU)
				maxU=temp.get((int)ellipse.get(i).x-1,(int) ellipse.get(i).y)[0];
			//System.out.println("test"+img.get((int)ellipse.get(i).x+1,(int) ellipse.get(i).y)[0]);
			sommD+=temp.get((int)ellipse.get(i).x+1,(int) ellipse.get(i).y)[0];
			if(temp.get((int)ellipse.get(i).x-1,(int) ellipse.get(i).y)[0]>maxU)
				maxD=temp.get((int)ellipse.get(i).x+1,(int)ellipse.get(i).y)[0];
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
	
	public static Mat resize(Mat img) {//permet aussi d'eviter les trop longs de chargement
		Mat res=new Mat();
		Size sz=new Size();
		if(img.height()>=2000) {
			sz=new Size(img.width()/5,img.height()/5);
		}
		else if(img.height()>=1600) {
			sz=new Size(img.width()/3,img.height()/3);
		}
		else if(img.height()>=1000) {
			sz=new Size(img.width()/1.5,img.height()/1.5);
		}
		/*
		else if(img.height()>=500) {
			sz=new Size(img.width()/2,img.height()/2);
		}*/
		Imgproc.resize( img, res, sz);
		return res;
	}

	public static void main(String[]args) {
		//drawEllipse();
		//drawEllipse(new Point(0,0),new Point(0,0));
		
		//CONVERTIR L'IMAGE DES LE DEBUT
		//System.exit(0);
		Mat img=loadPicture("E:\\image\\imageTest\\40.jpg");
		System.out.println("type"+img.type());
		//Imgproc.resize( img, img, sz );
		Mat img2=resize(img);
		Mat img3=new Mat();
		//Imgproc.cvtColor(img2, img2, Imgproc.COLOR_BGR2GRAY);
		show(img2,"resize");
		String path="E:\\image\\imageTest\\40.jpg";
		getEllipse(img2,path);
		Point startPos=getFirstPixel(img);
		//System.out.println("point de dep"+ startPos);
		Point droite=getRightPixel(img,(int)startPos.x,(int) startPos.y);
		//System.out.println("point de droite"+ droite);
		//drawEllipse(startPos,droite,img);
		
		Point gauche=getLeftPixel(img,93);
		System.out.println("gauche "+gauche);
		droite=getRightPixel(img,(int)gauche.x,(int) gauche.y);
		//drawEllipse(gauche,droite,path);
		
		System.out.println("finish");
	}
	

}
