/**
 * 
 */
package edu.uky.cs.acta225.flame;

import java.util.EventObject;

/**
 * 
 */
public class ProgressEvent extends EventObject {
	int progress;
	Integer total;
	String status;

	public ProgressEvent(Object source, int prog, Integer tot, String stat) {
		super(source);
		progress = prog;
		total = tot;
		status = stat;
	}
	
	public int getProgress() {
		return progress;
	}

	public Integer getTotal() {
		return total;
	}

	public String getStatus() {
		return status;
	}
}
