package edu.uky.cs.acta225.flameviewer;

public class EyefishVariation implements Variation {
	
	public EyefishVariation() {
	}

	@Override
	public Point calculate(double x, double y) {
		double multiplier = (2/(Math.sqrt(x*x+y*y)+1));
		return new Point(multiplier*x,multiplier*y);
	}

}
