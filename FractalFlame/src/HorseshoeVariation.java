
public class HorseshoeVariation implements Variation {
	
	public HorseshoeVariation() {
	}

	@Override
	public Point calculate(double x, double y) {
		double inverse_radius = 1/(x*x*+y*y);
		return new Point(inverse_radius*(x-y)*(x+y),2*x*y*inverse_radius);
	}

}
