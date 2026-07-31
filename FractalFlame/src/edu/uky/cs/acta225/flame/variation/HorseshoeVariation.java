package edu.uky.cs.acta225.flame.variation;

import edu.uky.cs.acta225.flame.Point;

public class HorseshoeVariation extends BasicVariation {
	public final static String NAME = "Horseshoe";
	
	public HorseshoeVariation() {
		this(NAME);
	}
	
	public HorseshoeVariation(String name) {
		super(name);
	}

	@Override
	public Point calculate(double x, double y) {
		double inverse_radius = 1/(x*x*+y*y);
		return new Point(inverse_radius*(x-y)*(x+y),2*x*y*inverse_radius);
	}
	
	public HorseshoeVariation cloneVariation() {
		return new HorseshoeVariation(name);
	}

	@Override
	public String getVariationTypeName() {
		return NAME;
	}

}
