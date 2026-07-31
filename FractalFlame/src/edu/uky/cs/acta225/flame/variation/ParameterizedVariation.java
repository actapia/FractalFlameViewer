package edu.uky.cs.acta225.flame.variation;

public abstract class ParameterizedVariation extends NamedVariation {
	protected Number[] parameters;
	
	public ParameterizedVariation(String n, Number[] params) {
		super(n);
		parameters = params;
	}
	
	public abstract int getNumParameters();
	
	@Override
	public Object accept(VariationVisitor visitor) {
		return visitor.visit(this);
	}
	
	public String getParameterName(int index) {
		return Character.toString((char) ((int)'a' + index));
	}
	
	public String[] getParameterNames() {
		String[] paramNames = new String[getNumParameters()];
		for (int i = 0; i < getNumParameters(); i++) {
			paramNames[i] = getParameterName(i);
		}
		return paramNames;
	}
	
	public Number getParameter(int index) {
		return parameters[index];
	}
	
	public Number[] getParameters() {
		return parameters.clone();
	}
	
	public abstract Number getMinimum(int index);
	public abstract Number getMaximum(int index);
	
	public Number[] getMinimums() {
		Number[] minimums = new Number[getNumParameters()];
		for (int i = 0; i < getNumParameters(); i++) {
			minimums[i] = getMinimum(i);
		}
		return minimums;
	}
	
	public Number[] getMaximums() {
		Number[] maximums = new Number[getNumParameters()];
		for (int i = 0; i < getNumParameters(); i++) {
			maximums[i] = getMaximum(i);
		}
		return maximums;
	}

	public void setParameter(int index, Number number) {
		parameters[index] = number;
	}

}
