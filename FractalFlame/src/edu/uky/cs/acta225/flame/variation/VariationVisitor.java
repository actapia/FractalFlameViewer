package edu.uky.cs.acta225.flame.variation;

public interface VariationVisitor<T> {
	public T visit(BasicVariation vari);
	public T visit(ParameterizedVariation vari);
}
