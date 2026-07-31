package edu.uky.cs.acta225.flame.variation;

import edu.uky.cs.acta225.flame.Named;
import edu.uky.cs.acta225.flame.Renamable;

public abstract class NamedVariation implements Variation, Renamable {
	protected String name;
	
	
	public NamedVariation(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String nm) {
		name = nm;
	}
	
	public abstract String getVariationTypeName();
	
	public abstract NamedVariation cloneVariation();
	
	@Override
	public String toString() {
		return getName(); 
	}
	
	public abstract Object accept(VariationVisitor visitor);
}
