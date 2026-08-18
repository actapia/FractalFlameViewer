package edu.uky.cs.acta225.flameviewer.events;

import java.util.EventObject;

public abstract class ChildEvent extends EventObject {
	EventObject parent;
	
	public EventObject getParent() {
		return parent;
	}

	public ChildEvent(Object source, EventObject p) {
		super(source);
		parent = p;
	}
}
