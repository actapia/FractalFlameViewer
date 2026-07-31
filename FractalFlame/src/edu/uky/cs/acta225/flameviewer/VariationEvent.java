package edu.uky.cs.acta225.flameviewer;

import java.util.EventObject;

import edu.uky.cs.acta225.flame.variation.NamedVariation;

public class VariationEvent extends ChildEvent {
	NamedVariation variation;
	
	public VariationEvent(Object source, EventObject p, NamedVariation vari) {
		super(source, p);
		variation = vari;

	}
	
	public NamedVariation getVariation() {
		return variation;
	}
}
