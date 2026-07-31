package edu.uky.cs.acta225.flame.variation;

import edu.uky.cs.acta225.flame.Point;

public class SphericalVariation extends BasicVariation {
	public static final String NAME = "Spherical";
	
	public SphericalVariation() {
		this(NAME);
	}
	
	public SphericalVariation(String name) {
		super(name);
	}
	
	@Override
	public Point calculate(double x, double y) {
		Point result = new Point();
		double radiusSquared = (x*x+y*y);
		result.setLocation(x/radiusSquared, y/radiusSquared);
		return result;
	}
	
	public SphericalVariation cloneVariation() {
		return new SphericalVariation(name);
	}

	@Override
	public String getVariationTypeName() {
		return NAME;
	}
}
