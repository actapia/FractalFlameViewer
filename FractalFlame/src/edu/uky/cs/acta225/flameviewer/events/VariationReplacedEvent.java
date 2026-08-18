package edu.uky.cs.acta225.flameviewer.events;

import java.util.EventObject;

import edu.uky.cs.acta225.flame.variation.NamedVariation;

public class VariationReplacedEvent extends VariationEvent {
	
	private NamedVariation newVariation;

	public NamedVariation getNewVariation() {
		return newVariation;
	}

	public VariationReplacedEvent(Object source, EventObject p, NamedVariation vari, NamedVariation newVari) {
		super(source, p, vari);
		newVariation = newVari;
	}

}
