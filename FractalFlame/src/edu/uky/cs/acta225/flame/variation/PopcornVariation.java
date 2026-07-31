package edu.uky.cs.acta225.flame.variation;

import edu.uky.cs.acta225.flame.Point;

public class PopcornVariation extends ParameterizedVariation {
	static final int NUM_PARAMETERS = 2;
	public static final String NAME = "Popcorn";
	private static String[] PARAMETER_NAMES = {"c", "f"};
	
	private static Number[] makeParamArray(double c, double f) {
		Number[] values = new Number[NUM_PARAMETERS];
		values[0] = c;
		values[1] = f;
		return values;
	}

	public PopcornVariation(String name, double c, double f) {
		super(name, makeParamArray(c, f));
	}
	
	public PopcornVariation(String name, Number[] parameters) {
		super(name, parameters);
		if (parameters.length != NUM_PARAMETERS) {
			throw new IllegalArgumentException(String.format("Popcorn must have exactly %d parameters.", NUM_PARAMETERS));
		}
	}
	
	public PopcornVariation(String name) {
		this(name, Math.random(), Math.random());
	}
	
	public PopcornVariation(double c, double f) {
		this(NAME, c, f);
	}
	
	public PopcornVariation() {
		this(NAME);
	}

	@Override
	public Point calculate(double x, double y) {
		return new Point(x+parameters[0].doubleValue()*Math.sin(Math.tan(3*y)),y+parameters[1].doubleValue()*Math.sin(Math.tan(3*x)));
	}
	
	public PopcornVariation cloneVariation() {
		return new PopcornVariation(name, (Double)parameters[0], (Double)parameters[1]);
	}

	@Override
	public String getVariationTypeName() {
		return NAME;
	}

	@Override
	public int getNumParameters() {
		return NUM_PARAMETERS;
	}
	
	@Override
	public String getParameterName(int index) {
		return PARAMETER_NAMES[index];
	}

	@Override
	public Number getMinimum(int index) {
		return -Double.MAX_VALUE;
	}

	@Override
	public Number getMaximum(int index) {
		return Double.MAX_VALUE;
	}

}
