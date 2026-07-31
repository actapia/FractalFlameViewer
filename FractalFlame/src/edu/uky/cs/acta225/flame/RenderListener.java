package edu.uky.cs.acta225.flame;

import java.util.EventListener;

public interface RenderListener extends EventListener {
	public void renderFinished(RenderEvent event);
}
