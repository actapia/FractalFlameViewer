package edu.uky.cs.acta225.flame;

import java.util.ArrayList;

abstract public class ProgressProcess {
	ArrayList<ProgressListener> progressListeners;
	
	public ProgressProcess() {
		progressListeners = new ArrayList<ProgressListener>();
	}
	
	public void addProgressListener(ProgressListener listener) {
		progressListeners.add(listener);
	}
	
	protected void progress(int soFar, Integer total, String status) {
		ProgressEvent event = new ProgressEvent(this, soFar, total, status);
		for (ProgressListener listener: progressListeners) {
			listener.progressUpdated(event);
		}
	}
	
	protected void progress(int soFar, Integer total) {
		progress(soFar, total, null);
	}
	
	protected void progress(int soFar) {
		progress(soFar, null);
	}
}
