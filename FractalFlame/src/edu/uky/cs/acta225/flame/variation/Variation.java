package edu.uky.cs.acta225.flame.variation;

import edu.uky.cs.acta225.flame.Point;

public interface Variation {
	public Point calculate(double x, double y);
	public Variation cloneVariation();
}
