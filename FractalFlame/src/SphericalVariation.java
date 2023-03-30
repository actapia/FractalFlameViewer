
public class SphericalVariation implements Variation{

	public SphericalVariation() {
	}
	@Override
	public Point calculate(double x, double y) {
		Point result = new Point();
		double radiusSquared = (x*x+y*y);
		result.setLocation(x/radiusSquared, y/radiusSquared);
		return result;
	}
	
}
