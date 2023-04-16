package edu.uky.cs.acta225.flameviewer;
public class PopcornVariation implements Variation {
	static final int NUM_PARAMETERS = 2;

	int last;
	double parameters[];

	public PopcornVariation(double c, double f) {
		parameters = new double[NUM_PARAMETERS];
		parameters[0] = c;
		parameters[1] = f;
	}
	
	public PopcornVariation() {
		this(Math.random(),Math.random());
	}

	@Override
	public Point calculate(double x, double y) {
		return new Point(x+parameters[0]*Math.sin(Math.tan(3*y)),y+parameters[1]*Math.sin(Math.tan(3*x)));
	}

}
