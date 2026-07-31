package edu.uky.cs.acta225.flameviewer;

import java.awt.GridBagConstraints;
import java.util.EventObject;

import edu.uky.cs.acta225.flame.variation.NamedVariation;

public class VariationDeletedEvent extends VariationEvent {
	public VariationDeletedEvent(Object source, EventObject p, NamedVariation vari) {
		super(source, p, vari);
	}
}
