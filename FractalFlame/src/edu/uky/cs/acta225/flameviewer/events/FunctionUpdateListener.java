package edu.uky.cs.acta225.flameviewer.events;

import java.util.EventListener;

public interface FunctionUpdateListener extends EventListener {
	public void functionChanged(FunctionUpdateEvent event);
}
