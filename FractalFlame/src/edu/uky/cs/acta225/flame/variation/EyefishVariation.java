package edu.uky.cs.acta225.flame.variation;

import edu.uky.cs.acta225.flame.Point;

public class EyefishVariation extends BasicVariation {
	private final static String NAME = "Eyefish";
	
	public EyefishVariation() {
		this(NAME);
	}
	
	public EyefishVariation(String name) {
		super(name);
	}

	@Override
	public Point calculate(double x, double y) {
		double multiplier = (2/(Math.sqrt(x*x+y*y)+1));
		return new Point(multiplier*x,multiplier*y);
	}
	
	public EyefishVariation cloneVariation() {
		return new EyefishVariation(name);
	}

	@Override
	public String getVariationTypeName() {
		return NAME;
	}
}
