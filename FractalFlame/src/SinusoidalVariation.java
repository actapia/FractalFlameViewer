

public class SinusoidalVariation implements Variation {
	
	public SinusoidalVariation() {
		
	}

	@Override
	public Point calculate(double x, double y) {
		Point result = new Point();
		result.setLocation(Math.sin(x), Math.sin(y));
		return result;
	}

}
