
public class SwirlVariation implements Variation {
	
	public SwirlVariation() {
		
	}

	@Override
	public Point calculate(double x, double y) {
		double radius = x*x*+y*y;
		return new Point(x*Math.sin(radius*radius)-y*Math.cos(radius*radius),x*Math.cos(radius*radius)-y*Math.sin(radius*radius));
	}
	
}
