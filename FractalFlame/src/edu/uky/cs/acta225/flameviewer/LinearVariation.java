package edu.uky.cs.acta225.flameviewer;


public class LinearVariation implements Variation {
	
	public LinearVariation() {
	}

	@Override
	public Point calculate(double x, double y) {
		Point result = new Point();
		result.setLocation(x, y);
		return result;
	}

}
