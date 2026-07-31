package edu.uky.cs.acta225.flameviewer;

import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JPanel;

import edu.uky.cs.acta225.flame.variation.NamedVariation;

public abstract class VariationControlPanel extends JPanel {
	
	private ArrayList<VariationChangeListener> changeListeners;
	
	public VariationControlPanel() {
		changeListeners = new ArrayList<VariationChangeListener>();
	}
	
	public void addVariationChangeListener(VariationChangeListener listener) {
		changeListeners.add(listener);
	}
	
	protected void sendChangeEvent(EventObject parent) {
		VariationChangeEvent event = new VariationChangeEvent(this, parent, getVariation());
		for (VariationChangeListener listener: changeListeners) {
			listener.variationChanged(event);
		}
	}
	
	public abstract NamedVariation getVariation();
}
