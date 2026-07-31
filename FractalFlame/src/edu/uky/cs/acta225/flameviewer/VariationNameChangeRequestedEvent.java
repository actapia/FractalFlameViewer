package edu.uky.cs.acta225.flameviewer;

import java.util.EventObject;

import edu.uky.cs.acta225.flame.variation.NamedVariation;

public class VariationNameChangeRequestedEvent extends VariationEvent {
	private String newName;
	
	public VariationNameChangeRequestedEvent(Object source, EventObject p, NamedVariation vari, String name) {
		super(source, p, vari);
		newName = name;
	}

	public String getNewName() {
		return newName;
	}
}
