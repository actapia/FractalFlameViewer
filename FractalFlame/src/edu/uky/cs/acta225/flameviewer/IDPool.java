package edu.uky.cs.acta225.flameviewer;

import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

public class IDPool {
	TreeMap<Integer, Boolean> available;
	
	public IDPool() {
		available = new TreeMap<Integer, Boolean>();
		available.put(0, false);
	}
	
	public int getNextID() {
		var iter = available.entrySet().iterator();
		while (iter.hasNext()) {
			var entry = iter.next();
			iter.remove();
			if (!entry.getValue().booleanValue()) {
				if (!available.containsKey(entry.getKey().intValue() + 1)) {
					available.put(entry.getKey().intValue() + 1, false);
				}
				return entry.getKey().intValue();
			}
		}
		return -1;
	}
	
	public void reserveID(int id) {
		var floorEntry = available.floorEntry(id);
		if ((floorEntry == null) || !floorEntry.getValue().booleanValue()) {
			available.put(id, true);
			if (available.containsKey(id + 1)) {
				if (available.get(id + 1).booleanValue()) {
					available.remove(id + 1);
				}
			}
			else {
				available.put(id + 1, false);
			}
		}
	}
	
	public void releaseID(int id) {
		var floorEntry = available.floorEntry(id);
		if ((floorEntry == null) || floorEntry.getValue().booleanValue()) {
			available.put(id, false);
			if (available.containsKey(id + 1)) {
				if (!available.get(id + 1).booleanValue() ) {
					available.remove(id + 1);
				}
			}
			else {
				available.put(id + 1, true);
			}
		}
	}
}
