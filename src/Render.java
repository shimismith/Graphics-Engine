import java.awt.Color;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Stack;

/**
 * @author Not just Russell Brown
 * @since April 4, 2016
 * 
 * This is the class that actually makes anything on screen appear.
 * It is what renders all the polygons on screen and textures them. 
 * 
 */

public class Render {
 
  /**
   * The distance at which to stop rendering pixels
   */
  
 public static double renderDistance = 2;
 
 //Various variables used in the rendering process
 protected static double[] zBuffer= new double[MainRun.width*MainRun.height];  //holds all the z values of the points on the screen

 
 /**
     * Finds the intersection between 2 lines
     * 
     * @param p1a point one on first line;
     * @param p1b point two on first line;
     * 
     * @param p2a point one on second line;
     * @param p2b point two on second line;
     * @return a point representing the point of intersection. 
     * 
 private double sizeT;
     */
 
 public Point find2DIntersection(Point p1a, Point p1b, Point p2a, Point p2b) {
	 //this method uses crazy matrix stuff
	 double x1 = p1a.getX();
	 double y1 = p1a.getY();
	 double x2 = p1b.getX();
	 double y2 = p1b.getY();
	 double x3 = p2a.getX();
	 double y3 = p2a.getY();
	 double x4 = p2b.getX();
	 double y4 = p2b.getY();

	 double d = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
	 if ((int)d == 0){
	  return null;
	 }
	 double xi = ((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d;
	 double yi = ((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d;
	 return new Point(xi,yi);
  
 }
 
 /**
     * Finds the intersection between a line and a plane
     * 
     * @param pa point one on the line;
     * @param pb point two on the line;
     * 
     * @return a point representing the point of intersection between the line and plane. 
     * 
     */
 
 public Point find3DIntersection(Point pa, Point pb) { // takes in two points on the line

  Point m = pa.sub(pb).makeUnitLength(); // direction vector of line the t value in the vector equation of the line

  double t = (renderDistance - pa.getZ()) / m.getZ();
  return new Point(pa.getX() + (t * m.getX()), pa.getY() + (t * m.getY()), renderDistance).return2DPoint();
 }

 /**
     * Renders a given rectangle on screen, after being projected into screen space and clipped. 
     * 
     * @param rectangle is the rectangle to be rendered.
     * 
     * @return void because the bufferedImage is automatically filled. 
     * 
     */
 
 public void resetZBuffer(){
	 double maxDistance= 80000;  //max distance of rendering- far clipping plane
	 Arrays.fill(zBuffer, maxDistance);
 }
 

private void rasterizeFlatTopTriangle(Point[] corners){
	// precondition: verticies sorted in ascending order by y
	int y = (int)(corners[1].getY());
	int bot = (int)(corners[0].getY());  // top of triangle
	double x1 = corners[1].getX();
	double x2 = corners[2].getX();
	
	double xInc1 = (corners[0].getX() - corners[1].getX()) / (corners[0].getY() - corners[1].getY());
	double xInc2 = (corners[0].getX() - corners[2].getX()) / (corners[0].getY() - corners[2].getY());
	
	for(; y <= bot; y++){
		renderScanLine(new Point(x1, y), new Point(x2, y), 0);
		x1 += xInc1;
		x2 += xInc2;
	}
}
private void rasterizeFlatBottomTriangle(Point[] corners){
	// precondition: verticies sorted in ascending order by y
	int y = (int)(corners[0].getY());
	int top = (int)(corners[2].getY());  // top of triangle
	double x1 = corners[0].getX();
	double x2 = corners[1].getX();
	
	double xInc1 = (corners[0].getX() - corners[2].getX()) / (corners[0].getY() - corners[2].getY());
	double xInc2 = (corners[1].getX() - corners[2].getX()) / (corners[1].getY() - corners[2].getY());
	
	for(; y >= top; y--){
		renderScanLine(new Point(x1, y), new Point(x2, y), 0);
		x1 -= xInc1;
		x2 -= xInc2;
	}
}

public void rasterizeTriangle(Point[] corners){
	// sort the corners
	Arrays.sort(corners);
	
	if(corners[1].getY() == corners[2].getY()){  // flat top
		rasterizeFlatTopTriangle(corners);
	}
	else if(corners[0].getY() == corners[1].getY()){  // flat bottom
		rasterizeFlatBottomTriangle(corners);
	}
	else{
		double x1, x3, x4, y1, y2, y3;
		x1 = corners[2].getX();
		x3 = corners[0].getX();
		y1 = corners[2].getY();
		y2 = corners[1].getY();
		y3 = corners[0].getY();
		
		x4 = ((y2 - y1)*(x3 - x1))/(y3 - y1) + x1;
//		if(x4 > MainRun.width){
//		System.out.println(x4);
//		}
		rasterizeFlatBottomTriangle(new Point[] {corners[1], new Point(x4, y2), corners[2]});
		rasterizeFlatTopTriangle(new Point[] {corners[0], corners[1], new Point(x4, y2)});
	}	
}
 
 
 public void renderScanLine(Point leftPoint, Point rightPoint, int scanLine){
	 //TODO: NOTE THAT CURRENTLY THE SCANLINE PARAMETER IS BEING SET TO 0 IN ALL APPLICATIONS OF THIS
	 
	 //draw line
//	 double deltaZ= rightPoint.sub(leftPoint).getZ() / rightPoint.sub(leftPoint).getX(); /*possibly / change in x */;  //used to keep track of z- it's the z component of the slope of the horizontal scan line
//	 double z= leftPoint.getZ();  //z value of the pixel being drawn
	 
	 int x = (int)leftPoint.getX();
	 int end = (int)rightPoint.getX();
	 if(x > end){
		 //they need to be swapped so we can draw from left to right
		 int temp = x; x = end; end = temp;
	 }
	 
	 for(; x <= end; x++){
		 //pixels in texture being loaded
		 /*int yTex= scanLine/4%tex.getHeight();  //takes the scanLine value and simplifies it into a number between 0 and tex.getHeight()
		 int xTex= x/4%tex.getWidth();
		 //z buffer
	      if((z<zBuffer[x + (((int) leftPoint.getY()) * MainRun.width)]) &&
	    		  tex.getCol()[xTex + yTex*tex.getWidth()].getAlpha()!=0){
	    	  MainRun.pixels[x + (int)leftPoint.getY() * MainRun.width]= tex.getCol()[xTex + yTex*tex.getWidth()].getRGB();
	        	   zBuffer[x + (((int) leftPoint.getY()) * MainRun.width)]= z;
	           }
	     z+= deltaZ;*/
		 try{  //might be out of bounds because no clipping 
			 MainRun.pixels[x + (int)leftPoint.getY() * MainRun.width]= /*tex.getCol()[xTex + yTex*tex.getWidth()].getRGB();//*/new Color(0, 28, 140).getRGB();
		 }
		 catch(Exception e){
		 }
	 }
 }
 
 public void bresenham(Point a, Point b, Color color){
//	 Color color = Color.BLUE;
	 int ax = (int) Math.round(a.getX());
	 int ay = (int) Math.round(a.getY());
	 int bx = (int) Math.round(b.getX());
	 int by = (int) Math.round(b.getY());
	 
	 boolean steep = false;  // true if the slope is greater than 1
	 
	 if(Math.abs(bx - ax) < Math.abs(by - ay)){  // if the slope is greater than 1
		 //swap x and y - making y the driving axis
		 int temp = ax; ax = ay; ay = temp;
		 temp = bx; bx = by; by = temp;
		 steep = true;
	 }

	 if(ax > bx){  // if the line is going right to left
		 //swap the points
		 int temp = ax; ax = bx; bx = temp;
		 temp = ay; ay = by; by = temp; 
	 }
	 
	// these are the values that are going to be incremented
	 int x = ax;
	 int y = ay;
	 
	 int dx = bx - ax;
	 int dy = by - ay;
	 
	 int yInc = 1;  // the amount that y is incremented by
	 
	 if(ay > by){  // if going down
		 dy = -dy;
		 yInc = -1;
	 }
	  
	 int e = 2*dy - dx;
	 int de1 = 2*dy;
	 int de2 = 2*dy - 2*dx;
	 
	 while(x <= bx){
		 try{  // use try in case it's out of the bounds - TODO add cliping
			 if(steep){
				 MainRun.pixels[y + x*MainRun.width] = color.getRGB();
			 }
			 else{
				 MainRun.pixels[x + y*MainRun.width] = color.getRGB();
			 }	
		 } 
		 catch(Exception exception){
		 }
		 
		 if(e <= 0){
			 e += de1;
		 }
		 else{
			 e += de2;
			 y += yInc;
		 }
		 x += 1;
	 }
 }

 
 //points in polygon need to be in clockwise order
 public void triangulateConvexPolygon(Point[] polygon){  //uses ear clipping- since it's a convex polygon is takes linear time

	 rasterizeTriangle(Arrays.copyOfRange(polygon, 0, 3));  //makes first triangle with indexes 0, 1 ,2 from polygon
	 int i= 2;
	 for(int trianglesMade= 1; trianglesMade< polygon.length-2; trianglesMade++){
		 rasterizeTriangle(new Point[]{polygon[0], polygon[i], polygon[i+1]});
		 i++;
	 }
	 
 }
 
 public void triangulateUnimonotonePolygon(Point[] polygon){  //first and last index are the two verticies of the straight edge
	 Stack<Point> stack= new Stack<Point>();
	 Point[][] triangles= new Point[polygon.length-2][3];
	 for(int i=0; i<polygon.length; i++){
		
		 if(stack.size()>=2){  //then we should check for an ear with the 3 vertices at top of stack-- the third is the new one that we are on now (polygon[i])
			 
			 Point vector1= stack.get(stack.size()-1).sub(stack.get(stack.size()-2));//stack.get(stack.size()-2).sub(stack.get(stack.size()-1)); //first vertex - middle vertex
			 Point vector2= stack.get(stack.size()-1).sub(polygon[i]); //polygon[i].sub(stack.get(stack.size()-1));  //third vertex - middle vertex
			
			double fDotProduct = vector1.getX() * vector2.getX() + vector1.getY() * vector1.getY();
		    double fPerpDotProduct = vector1.getX() * vector2.getY() - vector1.getY() * vector2.getX();
		    double angle= Math.atan2(fPerpDotProduct, fDotProduct);
		   
			if(angle<0){
				//rasterize triangle
				rasterizeTriangle(new Point[]{stack.get(stack.size()-2), stack.get(stack.size()-1), polygon[i]});
				stack.pop();
			}
			
		 }
		 stack.push(polygon[i]);  //push on new vertex  
	 }
	 
	 if(stack.size()>0){
		 //load convex chunk into array to be triangulated
		 Point[] convexPolygon= new Point[stack.size()];
	 	for(int i=0; i<convexPolygon.length; i++){
		 	convexPolygon[i]= stack.pop();
	 	}
	 
	 	triangulateConvexPolygon(convexPolygon);
	 }
 }
 
 public void trapeziodDecomposition(Point[] polygon){
	 for(int i=0; i<polygon.length; i++){
		 //if the two adjacent corners are both in the left or right and it is an ear this edge extends off the polygon and cannot be used to make a trapezoid
	 }
 }
 
 
 public void rasterizeCircle(Point center, double r){
	
	 for(int y= (int)(center.getY()-r); y<center.getY()+r; y++){ 
		 Point leftPoint= new Point(-Math.sqrt((r*r)-((y-center.getY())*(y-center.getY()))) + center.getX(), y);
		 Point rightPoint= new Point(Math.sqrt((r*r)-((y-center.getY())*(y-center.getY()))) + center.getX(), y);
		 renderScanLine(leftPoint, rightPoint, 0);
	 }


 }
 
//private LightSource l = new LightSource(0, 0, 0);

 public void renderMesh(Point[][] mesh, double yRotate){  //mesh is the triangles returned from ObjParser
	 for(int i=0; i< mesh.length; i++){
		 Point p1 = new Point(mesh[i][0]);
		 Point p2 = new Point(mesh[i][1]);
		 Point p3 = new Point(mesh[i][2]);
		 
		 //TODO: build this into the obj parser - adjust all the points properly
		 double yMiddle = 0.23625; //this is the origin for the teapot
//		 double yMiddle = 0.9630295030000001/2; //this is the origin for the bunny
		 
		 //shift
		 p1.setY(p1.getY() - yMiddle);
		 p2.setY(p2.getY() - yMiddle);
		 p3.setY(p3.getY() - yMiddle);
		 
		 //also shift it down a bit to look better THIS IS ONYL FOR THE BUNNY
//		 double shift = 10;
//		 p1.setY(p1.getY() + shift);
//		 p2.setY(p2.getY() + shift);
//		 p3.setY(p3.getY() + shift);
		 
		
		 //rotate
		 p1.rotateX(yRotate);
		 p2.rotateX(yRotate);
		 p3.rotateX(yRotate);
		 p1.rotateZ(yRotate);
		 p2.rotateZ(yRotate);
		 p3.rotateZ(yRotate);	
		 p1.rotateY(yRotate);
		 p2.rotateY(yRotate);
		 p3.rotateY(yRotate);
		 
		 //shift back
		 p1.setY(p1.getY() + yMiddle);
		 p2.setY(p2.getY() + yMiddle);
		 p3.setY(p3.getY() + yMiddle);
		 		
		 //adjust color by lighting
//		 l.adjustBrightness(p1);
//		 l.adjustBrightness(p2);
//		 l.adjustBrightness(p3);

		 p1 = p1.return2DPoint();
		 p2 = p2.return2DPoint();
		 p3 = p3.return2DPoint();
	 
		 
		 rasterizeTriangle(new Point[]{p1, p2, p3});
	 }
 }

}