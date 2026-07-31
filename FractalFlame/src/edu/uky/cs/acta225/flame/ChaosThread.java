package edu.uky.cs.acta225.flame;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JProgressBar;

import edu.uky.cs.acta225.imageutils.ImageUtilities;

public class ChaosThread extends ProgressProcess implements Runnable {

	int[][] densityHistogram;
	int[][] colorHistogram;
	int densityMax;
	double zoomFactor,gamma;
	int xOffset, yOffset, iterations, supersampleLevel;
	boolean ended;
//	IteratedFunction[] functions;
	Distribution<IteratedFunction> functionProbabilities;
//	JProgressBar progressBar;
	Thread threaddy;

	
	public ChaosThread(int histogramHeight,int histogramWidth,double gam,int iter,Distribution<IteratedFunction> fProbs, double zoom, int x, int y, int ssLevel) {
		super();
		densityHistogram = new int[histogramHeight][histogramWidth];
		colorHistogram = new int[histogramHeight][histogramWidth];
		densityMax = 1;
		zoomFactor = zoom;
		gamma = gam;
		xOffset = x;
		yOffset = y;
		supersampleLevel = ssLevel;
		iterations = iter;
		ended = false;
		functionProbabilities = fProbs;
		threaddy = new Thread(this);
		threaddy.setDaemon(true);
		threaddy.start();
	}

	public int[][] getDensityHistogram() {
		return densityHistogram;
	}
	
	public int[][] getColorHistogram() {
		return colorHistogram;
	}
	
	public int getDensityMax() {
		return densityMax;
	}
	
	public Thread getThread() {
		return threaddy;
	}
	
	private synchronized void chaosGame() {	
		//Rows are y and columns are x in the histograms.
		double lastX = (int)(Math.random()*densityHistogram[0].length);
		double lastY = (int)(Math.random()*densityHistogram.length);
		double inverse_gamma = 1/gamma;
		int currentColor = ImageUtilities.combine(ImageUtilities.MAX_COLOR,(int)(Math.random()*ImageUtilities.MAX_COLOR),(int)(Math.random()*ImageUtilities.MAX_COLOR),(int)(Math.random()*ImageUtilities.MAX_COLOR));;
		progress(0, iterations, "Iterating...");
		//		System.out.println("functions[0].getConstantA(): "+functions[0].getConstantA());
		int progressValue = 0;
		while (iterations-- > 0) {
			if (ended)
				return;
			else {
				//System.out.println("lastX: "+lastX);
				Point resultingPoint;
				IteratedFunction f = functionProbabilities.sample();
				//Pick a function.
				

				resultingPoint = f.calculate(lastX, lastY);

				lastX = resultingPoint.getX();
				
				lastY = resultingPoint.getY();
				int pixelX = (int)((lastX*zoomFactor+xOffset)*supersampleLevel);
				int pixelY = (int)((lastY*zoomFactor+yOffset)*supersampleLevel);
//				System.out.println("pixelX: "+pixelX);
//				System.out.println("const a: "+f.getConstantA());
				currentColor = RunnableChaosGame.blendColors(f.getColor(),currentColor);
				if ((pixelX < densityHistogram[0].length) && (pixelY < densityHistogram.length) && (pixelX >= 0) && (pixelY >= 0)) {
					densityHistogram[pixelY][pixelX]+=1;
					if (densityMax < densityHistogram[pixelY][pixelX])
						densityMax = densityHistogram[pixelY][pixelX];
					colorHistogram[pixelY][pixelX] = RunnableChaosGame.blendColors(colorHistogram[pixelY][pixelX],currentColor);
				}
				progress(progressValue++);
//				System.out.println(progressValue);
			}
		}
	}
	
	@Override
	public void run() {
		chaosGame();
	}
	
	public void end() {
		ended = true;
	}

}
