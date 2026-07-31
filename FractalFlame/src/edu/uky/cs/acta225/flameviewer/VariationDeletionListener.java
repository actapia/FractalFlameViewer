package edu.uky.cs.acta225.flameviewer;

import java.util.EventListener;

public interface VariationDeletionListener extends EventListener {
	public void variationDeleted(VariationDeletedEvent event);
}
