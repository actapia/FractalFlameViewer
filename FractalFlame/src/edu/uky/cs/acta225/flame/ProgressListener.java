package edu.uky.cs.acta225.flame;

import java.util.EventListener;

public interface ProgressListener extends EventListener {
	void progressUpdated(ProgressEvent event);
}
