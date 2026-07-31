package edu.uky.cs.acta225.flame.variation;

import edu.uky.cs.acta225.flame.Point;

public class SwirlVariation extends BasicVariation {
	public static final String NAME = "Swirl";
	
	public SwirlVariation() {
		this(NAME);
	}
	
	public SwirlVariation(String name) {
		super(name);
	}

	@Override
	public Point calculate(double x, double y) {
		double radius = x*x*+y*y;
		return new Point(x*Math.sin(radius*radius)-y*Math.cos(radius*radius),x*Math.cos(radius*radius)-y*Math.sin(radius*radius));
	}
	
	public SwirlVariation cloneVariation() {
		return new SwirlVariation(name);
	}

	@Override
	public String getVariationTypeName() {
		return NAME;
	}
}
