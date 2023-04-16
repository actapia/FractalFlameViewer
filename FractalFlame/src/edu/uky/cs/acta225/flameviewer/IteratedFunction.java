package edu.uky.cs.acta225.flameviewer;
import java.awt.Color;

import edu.uky.cs.acta225.imageutils.ImageUtilities;

public class IteratedFunction {
	public static final int VARIATION_LINEAR = 0;
	public static final int VARIATION_SINUSOIDAL = 1;
	public static final int VARIATION_SPHERICAL = 2;
	public static final int VARIATION_SWIRL = 3;
	public static final int VARIATION_POPCORN = 4;
	public static final int VARIATION_HORSESHOE = 5;
	public static final int VARIATION_EYEFISH = 6;
	public static final int NUMBER_OF_VARIATIONS = 7;
	private double a,b,c,d,e,f;
	private int color;
	private Variation[] V;
	private double[] w;

	
	public IteratedFunction(double[] maxVariationWeights) {	
		//Initialize everything to random values.
		a = (Math.random()*2)-1;
		b = (Math.random()*2)-1;
		c = (Math.random()*2)-1;
		d = (Math.random()*2)-1;
		e = (Math.random()*2)-1;
		f = (Math.random()*2)-1;
		color = ImageUtilities.combine(ImageUtilities.MAX_COLOR,(int)(Math.random()*ImageUtilities.MAX_COLOR),(int)(Math.random()*ImageUtilities.MAX_COLOR),(int)(Math.random()*ImageUtilities.MAX_COLOR));
		V = new Variation[NUMBER_OF_VARIATIONS];
		V[VARIATION_LINEAR] = new LinearVariation();
		V[VARIATION_SINUSOIDAL] = new SinusoidalVariation();
		V[VARIATION_SPHERICAL] = new SphericalVariation();
		V[VARIATION_SWIRL] = new SwirlVariation();
		V[VARIATION_POPCORN] = new PopcornVariation();
		V[VARIATION_HORSESHOE] = new HorseshoeVariation();
		V[VARIATION_EYEFISH] = new EyefishVariation();
		w = new double[V.length];
		for (int i=0;i<V.length;i++)
			w[i] = Math.random()*maxVariationWeights[i];
	}
	
	public IteratedFunction(double presetA, double presetB, double presetC, double presetD, double presetE, double presetF, double[] weights, int col) {
		a = presetA;
		b = presetB;
		c = presetC;
		d = presetD;
		e = presetE;
		f = presetF;
		V = new Variation[NUMBER_OF_VARIATIONS];
		V[VARIATION_LINEAR] = new LinearVariation();
		V[VARIATION_SINUSOIDAL] = new SinusoidalVariation();
		V[VARIATION_SPHERICAL] = new SphericalVariation();
		V[VARIATION_SWIRL] = new SwirlVariation();
		V[VARIATION_POPCORN] = new PopcornVariation();
		V[VARIATION_HORSESHOE] = new HorseshoeVariation();
		V[VARIATION_EYEFISH] = new EyefishVariation();
		w = new double[V.length];
		color = col;
		if (weights.length != V.length) {
			throw new IllegalArgumentException("Weight array needs to have length "+V.length);
		}
		else {
			for (int weightIndex=0;weightIndex<weights.length;weightIndex++) {
				w[weightIndex] = weights[weightIndex];
			}
		}
	}
	
	public double getConstantA() {
		return a;
	}
	
	public double getConstantB() {
		return b;
	}
	
	public double getConstantC() {
		return c;
	}
	
	public double getConstantD() {
		return d;
	}
	
	public double getConstantE() {
		return e;
	}
	
	public double getConstantF() {
		return f;
	}
	
	public double[] getVariationWeights() {
		return w;
	}
	
	public Point calculate(double x, double y) {
		double sumX = 0;
		double sumY = 0;
		for (int i=0;i<V.length;i++) {
			Point variationResult = V[i].calculate(a*x+b*y+c, d*x+e*y+f);
			sumY+=w[i]*variationResult.getY();
			sumX+=w[i]*variationResult.getX();
		}
		Point result = new Point();
		result.setLocation(sumX,sumY);
		return result;			
	}
	
	public int getColor() {return color;}
	
	public void setColor(int col) {
		color = col;
	}
}
