
public class Point {
	private double x,y;
	public  Point(double xx, double yy) {
		x = xx;
		y = yy;
	}
	public Point() {
		
	}
	public double getX() {return x;}
	public double getY() {return y;}
	public void setX(double xx) {x = xx;}
	public void setY(double yy) {y=yy;}
	public void setLocation(double xx,double yy) {
		x = xx;
		y = yy;
	}
	public String toString() {
		return x+", "+y;
	}
	
	
}
