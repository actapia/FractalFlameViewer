package edu.uky.cs.acta225.flame.variation;

import edu.uky.cs.acta225.flame.Point;

public class SinusoidalVariation extends BasicVariation {
	public static final String NAME = "Sinusoidal";
	
	public SinusoidalVariation() {
		this(NAME);
	}
	
	public SinusoidalVariation(String name) {
		super(name);
	}

	@Override
	public Point calculate(double x, double y) {
		Point result = new Point();
		result.setLocation(Math.sin(x), Math.sin(y));
		return result;
	}
	
	public SinusoidalVariation cloneVariation() {
		return new SinusoidalVariation(name);
	}

	@Override
	public String getVariationTypeName() {
		return NAME;
	}

}
