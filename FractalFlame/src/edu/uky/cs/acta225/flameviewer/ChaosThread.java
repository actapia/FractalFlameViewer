package edu.uky.cs.acta225.flameviewer;
import javax.swing.JProgressBar;

import edu.uky.cs.acta225.imageutils.ImageUtilities;

public class ChaosThread implements Runnable {

	int[][] densityHistogram;
	int[][] colorHistogram;
	int densityMax;
	double zoomFactor,gamma;
	int xOffset, yOffset, iterations, supersampleLevel;
	boolean ended;
	IteratedFunction[] functions;
	double[] functionProbabilities;
	JProgressBar progressBar;
	Thread threaddy;
	public ChaosThread(int histogramHeight,int histogramWidth,double gam,int iter,IteratedFunction[] funs,double[] fProbs, double zoom, int x, int y, int ssLevel,JProgressBar prog) {
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
		functions = funs;
		functionProbabilities = fProbs;
		progressBar = prog;
		threaddy = new Thread(this);
		threaddy.setDaemon(true);
		threaddy.start();
		// TODO Auto-generated constructor stub
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
		if (progressBar != null) {
			progressBar.setValue(0);
			progressBar.setMaximum(iterations);
			progressBar.setString("Iterating...");
		}
		//		System.out.println("functions[0].getConstantA(): "+functions[0].getConstantA());
		while (iterations-- > 0) {
			if (ended)
				return;
			else {
				//System.out.println("lastX: "+lastX);
				Point resultingPoint;
				IteratedFunction f = null;
				//Pick a function.
				double randomValue = Math.random();
				double lastProbability = 0;
				int functionNumber=0;
				
				while ((functionNumber<functions.length) && (f==null)) {
					if (randomValue>=lastProbability && randomValue<(lastProbability+functionProbabilities[functionNumber])) {
						f = functions[functionNumber];
					}
					lastProbability+=functionProbabilities[functionNumber];
					functionNumber++;
				}
				resultingPoint = f.calculate(lastX, lastY);
//				if (Double.isNaN(resultingPoint.getX()) ) {
//					System.out.println("a "+f.getConstantA());
//					System.out.println("b "+f.getConstantB());
//					System.out.println("c "+f.getConstantC());
//					System.out.println("d "+f.getConstantD());
//					System.out.println("e "+f.getConstantE());
//					System.out.println("f "+f.getConstantF());
//					double[] weights = f.getVariationWeights();
//					for (int w=0;w<weights.length;w++) {
//						System.out.print("w["+w+"]: "+weights[w]);
//					}
//					JOptionPane.showMessageDialog(null,"lastX is NaN from f("+lastX+","+lastY+") on iteration " + iterations + ". See output.");
//					
//				}
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
				if (progressBar != null) {
					progressBar.setValue(progressBar.getValue()+1);
				}
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
