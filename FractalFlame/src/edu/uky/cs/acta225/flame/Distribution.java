package edu.uky.cs.acta225.flame;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.collections4.map.LinkedMap;

public class Distribution<T> {
	private LinkedMap<T, Integer> probabilities;
	private int sum = 0;
	
	public Distribution() {
		probabilities = new LinkedMap<T, Integer>();
	}
	
	public Distribution(Distribution existing) {
		this();
		probabilities = existing.probabilities.clone();
		sum = existing.sum;
	}
	
	
	public Distribution<T> clone() {
		return new Distribution(this);
	}
	
	public int indexOf(T key) {
		return probabilities.indexOf(key);
	}
	
	public Set<T> keySet() {
		return probabilities.keySet();
	}
	
	public Collection<Integer> values() {
		return probabilities.values();
	}
	
	public synchronized void put(T key, Integer value) {
		sum -= probabilities.getOrDefault(key, Integer.valueOf(0));
		sum += value;
		probabilities.put(key, value);
	}
	
	public Integer get(T key) {
		return probabilities.get(key);
	}
	
	public void remove(T key) {
		sum -= get(key);
		probabilities.remove(key);
	}
	
	public T sample() {
		int oldSum = sum;
		double value = Math.random() * sum;
		double lastProbability = 0;
		T selected = null;
		var iter = probabilities.entrySet().iterator();
		
		while ((selected == null) && iter.hasNext()) {
			var entry = iter.next();
			lastProbability += entry.getValue().intValue();
			if (value <= lastProbability) {
				selected = entry.getKey();
			}
		}
		return selected;
	}
	
	public int size() {
		return probabilities.size();
	}
}
