package edu.uky.cs.acta225.flameviewer;

import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JPanel;

import edu.uky.cs.acta225.flame.variation.NamedVariation;

public abstract class VariationDeletingPanel extends JPanel {
	private ArrayList<VariationDeletionListener> deletionListeners;
	
	public VariationDeletingPanel() {
		super();
		deletionListeners = new ArrayList<VariationDeletionListener>();
	}
	
	protected void sendDeletedEvent(VariationDeletedEvent event) {
		for (VariationDeletionListener listener: deletionListeners) {
			listener.variationDeleted(event);
		}
	}
	
	protected void sendDeletedEvent(EventObject child, NamedVariation vari) {
		sendDeletedEvent(new VariationDeletedEvent(this, child, vari));
	}
	
	public void addDeletionListener(VariationDeletionListener listener) {
		deletionListeners.add(listener);
	}
}
