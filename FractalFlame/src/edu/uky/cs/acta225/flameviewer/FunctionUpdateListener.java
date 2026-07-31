package edu.uky.cs.acta225.flameviewer;

import java.util.EventListener;

public interface FunctionUpdateListener extends EventListener {
	public void functionChanged(FunctionUpdateEvent event);
}
