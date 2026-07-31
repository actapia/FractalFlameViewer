package edu.uky.cs.acta225.flame.variation;

import edu.uky.cs.acta225.flame.Point;

public abstract class BasicVariation extends NamedVariation {
	public BasicVariation(String n) {
		super(n);
	}
	
	@Override
	public Object accept(VariationVisitor visitor) {
		return visitor.visit(this);
	}
}
