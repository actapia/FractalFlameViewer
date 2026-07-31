package edu.uky.cs.acta225.flame.variation;

import edu.uky.cs.acta225.flame.Point;

public class LinearVariation extends BasicVariation {
	public final static String NAME = "Linear";
	
	public LinearVariation() {
		this(NAME);
	}
	
	public LinearVariation(String name) {
		super(name);
	}

	@Override
	public Point calculate(double x, double y) {
		Point result = new Point();
		result.setLocation(x, y);
		return result;
	}

	public LinearVariation cloneVariation() {
		return new LinearVariation(name);
	}

	@Override
	public String getVariationTypeName() {
		return NAME;
	}
}
