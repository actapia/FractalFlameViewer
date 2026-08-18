package edu.uky.cs.acta225.flame;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.uky.cs.acta225.flame.variation.NamedVariation;
import edu.uky.cs.acta225.imageutils.ImageUtilities;
import edu.uky.cs.acta225.linkedlist.MyLinkedHashMap;

public class IteratedFunction implements Renamable {
//	public static final int VARIATION_LINEAR = 0;
//	public static final int VARIATION_SINUSOIDAL = 1;
//	public static final int VARIATION_SPHERICAL = 2;
//	public static final int VARIATION_SWIRL = 3;
//	public static final int VARIATION_POPCORN = 4;
//	public static final int VARIATION_HORSESHOE = 5;
//	public static final int VARIATION_EYEFISH = 6;
//	public static final int NUMBER_OF_VARIATIONS = 7;
//	private double a,b,c,d,e,f;
	private double[] parameters;
	private int color;
	private MyLinkedHashMap<NamedVariation, Double> variationWeights;
	private String name;

	
	public IteratedFunction(String nm) {	
		//Initialize everything to random values.
		this((Math.random()*2)-1, (Math.random()*2)-1, (Math.random()*2)-1, (Math.random()*2)-1, (Math.random()*2)-1, (Math.random()*2)-1, ImageUtilities.combine(ImageUtilities.MAX_COLOR,(int)(Math.random()*ImageUtilities.MAX_COLOR),(int)(Math.random()*ImageUtilities.MAX_COLOR),(int)(Math.random()*ImageUtilities.MAX_COLOR)), nm);
	}
	
	public IteratedFunction(double[] params, int col, Map<NamedVariation, Double> varWeights, String nm) {
		parameters = params;
		color = col;
		variationWeights = new MyLinkedHashMap<NamedVariation, Double>();
		for (var entry: varWeights.entrySet()) {
			variationWeights.put(entry.getKey(), entry.getValue());
		}
		name = nm;
	}
	
	
	public IteratedFunction(double presetA, double presetB, double presetC, double presetD, double presetE, double presetF, int col, String nm) {
		this(presetA, presetB, presetC, presetD, presetE, presetF, col, new LinkedHashMap<NamedVariation, Double>(), nm);
	}
	
	private static double[] makeParams(double presetA, double presetB, double presetC, double presetD, double presetE, double presetF) {
		double[] params = {presetA, presetB ,presetC, presetD, presetE, presetF};
		return params;
	}
	
	public IteratedFunction(double presetA, double presetB, double presetC, double presetD, double presetE, double presetF, int col, Map<NamedVariation, Double> varWeights, String nm) {
		this(makeParams(presetA, presetB, presetC, presetD, presetE, presetF), col, varWeights, nm);
	}
	
	public IteratedFunction clone() {
		return new IteratedFunction(parameters.clone(), color, new LinkedHashMap<NamedVariation, Double>(variationWeights), name);
	}
	
	public int numParameters() {
		return parameters.length;
	}
	
	public IteratedFunction deepClone() {
		MyLinkedHashMap<NamedVariation, Double> weights = new MyLinkedHashMap<NamedVariation, Double>();
		for (var entry: variationWeights.entrySet()) {
			weights.put(entry.getKey().cloneVariation(), entry.getValue());
		}
		return new IteratedFunction(parameters.clone(), color, weights, name);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String nm) {
		name = nm;
	}
	
	public double getParameter(int index) {
		return parameters[index];
	}
	
	public void setParameter(int index, double value) {
		parameters[index] = value;
	}
	
	public void removeVariation(NamedVariation variation) {
		variationWeights.remove(variation);
	}
	
	public Set<NamedVariation> getVariations() {
		return variationWeights.keySet();
	}
	
	public Map<NamedVariation, Double> getVariationWeights() {
		return Collections.unmodifiableMap(variationWeights);
	}
	
	public void replaceVariation(NamedVariation oldVariation, NamedVariation newVariation) {
		variationWeights.replaceKey(oldVariation, newVariation);
	}
	
	public void addVariation(NamedVariation variation, Double weight) {
		variationWeights.put(variation, weight);
	}
	
	public void setVariationWeight(NamedVariation variation, Double weight) {
		variationWeights.put(variation, weight);
	}
	
	public double getConstantA() {
		return parameters[0];
	}
	
	public double getConstantB() {
		return parameters[1];
	}
	
	public double getConstantC() {
		return parameters[2];
	}
	
	public double getConstantD() {
		return parameters[3];
	}
	
	public double getConstantE() {
		return parameters[4];
	}
	
	public double getConstantF() {
		return parameters[5];
	}
	
	public static IteratedFunction randomFromLimits(Map<? extends NamedVariation, Double> varWeightLimits, String nm) {
		IteratedFunction f = new IteratedFunction(nm);
		for (var entry: varWeightLimits.entrySet()) {
			f.variationWeights.put(entry.getKey(), entry.getValue().doubleValue() * Math.random());
		}
		return f;
	}
	
	public Point calculate(double x, double y) {
		double sumX = 0;
		double sumY = 0;
		for (var entry: variationWeights.entrySet()) {
			Point variationResult = entry.getKey().calculate(getConstantA()*x+getConstantB()*y+getConstantC(), getConstantD()*x+getConstantE()*y+getConstantF());
			sumY+=entry.getValue().doubleValue()*variationResult.getY();
			sumX+=entry.getValue().doubleValue()*variationResult.getX();
		}
		Point result = new Point();
		result.setLocation(sumX, sumY);
		return result;			
	}
	
	public int getColor() {return color;}
	
	public void setColor(int col) {
		color = col;
	}
}
