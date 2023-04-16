package edu.uky.cs.acta225.flameviewer;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import edu.uky.cs.acta225.imageutils.ImageUtilities;

public class RunnableChaosGame implements Runnable {
	FlameDisplay displayer;
	double zoomFactor, gamma;
	int xOffset, yOffset, iterations, supersampleLevel;
	BufferedImage img;
	IteratedFunction[] functions;
	JProgressBar progressBar;
	boolean ended;
	double[] functionProbabilities;
	public RunnableChaosGame(FlameDisplay disp, JProgressBar prog, BufferedImage i, int iter, IteratedFunction[] ifs, double zoom, double g, int x, int y, int ssLevel, double[] probabilities) {
		displayer = disp;
		progressBar = prog;
		img = i;
		iterations = iter;
		functions = ifs;
		zoomFactor = zoom;
		gamma = g;
		xOffset = x;
		yOffset = y;
		supersampleLevel = ssLevel;
		ended = false;
		functionProbabilities = probabilities;
	}
	
	public void end() {
		ended = true;
	}
	
	public static int blendColors(int[] colors) {
		int sum_r = 0;
		int sum_g = 0;
		int sum_b = 0;
		for (int color_index=0;color_index<colors.length;color_index++) {
			sum_r+=ImageUtilities.getRedComponent(colors[color_index]);
			sum_g+=ImageUtilities.getGreenComponent(colors[color_index]);
			sum_b+=ImageUtilities.getBlueComponent(colors[color_index]);
		}
		int average_r = sum_r/colors.length;
		int average_g = sum_g/colors.length;
		int average_b = sum_b/colors.length;
		return ImageUtilities.combine(ImageUtilities.MAX_COLOR, average_r, average_g, average_b);
	}
	
	private int getSupersampledColor(int[][] histogram, int x, int y) {
		int[] samples = new int[supersampleLevel*supersampleLevel];
		int sample_number = 0;
		for (int histogram_row=(y*supersampleLevel);histogram_row<((y+1)*supersampleLevel);histogram_row++) {
			for (int histogram_column=(x*supersampleLevel);histogram_column<((x+1)*supersampleLevel);histogram_column++) {
				samples[sample_number++] = histogram[histogram_row][histogram_column];
			}
		}
		return RunnableChaosGame.blendColors(samples);
	}
	
	private int getSupersampledDensity(int[][] histogram, int x, int y) {
		int average = 0;
		for (int histogram_row=(y*supersampleLevel);histogram_row<((y+1)*supersampleLevel);histogram_row++) {
			for (int histogram_column=(x*supersampleLevel);histogram_column<((x+1)*supersampleLevel);histogram_column++) {
				average+= histogram[histogram_row][histogram_column];
			}
		}
		return average/(supersampleLevel*supersampleLevel);
	}
	public static int blendColors(int col1, int col2) {
		int average_r = (ImageUtilities.getRedComponent(col1)+ImageUtilities.getRedComponent(col2))/2;
		int average_g = (ImageUtilities.getGreenComponent(col1)+ImageUtilities.getGreenComponent(col2))/2;
		int average_b = (ImageUtilities.getBlueComponent(col1)+ImageUtilities.getBlueComponent(col2))/2;
		return ImageUtilities.combine(ImageUtilities.MAX_COLOR, average_r, average_g, average_b);
	}
	private void old_chaosGame() {
	final int N_GON = 3;
	double lastX = (int)(Math.random()*img.getWidth());
	double lastY = (int)(Math.random()*img.getHeight());
	img.setRGB((int)lastX, (int)lastY, ImageUtilities.combine(255,255,255,255));
	//Choose a random vertex.
	int lastVertex = -1;
	Point[] vertices = new Point[N_GON];
	for (int i=0;i<vertices.length;i++) {
		double angle = i*2*(Math.PI/vertices.length);
		vertices[i] = new Point();
		vertices[i].setLocation((img.getWidth()*(Math.sin(angle)+1))/2,(img.getHeight()*(Math.cos(angle)+1))/2);
	}
	while (iterations-- > 0) {
		Point resultingPoint;
		Point vertex = vertices[(int)(Math.random()*vertices.length)];
		lastX = ((vertex.getX()+lastX)/2);
		lastY = ((vertex.getY()+lastY)/2);
		int pixelX = (int)lastX;
		int pixelY = (int)lastY;
		if ((pixelX < img.getWidth()) && (pixelY < img.getHeight()) && (pixelX >= 0) && (pixelY >= 0))
			img.setRGB(pixelX, pixelY, ImageUtilities.combine(255,255,255,255));
	}
}

	private synchronized void chaosGame() {
		
		//Rows are y and columns are x in the histograms.
		int histogramHeight = img.getHeight()*supersampleLevel;
		int histogramWidth = img.getWidth()*supersampleLevel;
		int[][] densityHistogram = new int[histogramHeight][histogramWidth];
		int[][] colorHistogram = new int[histogramHeight][histogramWidth];
		double lastX = (int)(Math.random()*histogramWidth);
		double lastY = (int)(Math.random()*histogramHeight);
//		System.out.println("starting with lastX "+lastX);
		double inverse_gamma = 1/gamma;
//		progressBar.setValue(0);
//		progressBar.setMaximum(iterations);
//		progressBar.setString("Iterating...");
		int currentColor = ImageUtilities.combine(ImageUtilities.MAX_COLOR,(int)(Math.random()*ImageUtilities.MAX_COLOR),(int)(Math.random()*ImageUtilities.MAX_COLOR),(int)(Math.random()*ImageUtilities.MAX_COLOR));;
		int cores = Runtime.getRuntime().availableProcessors();
		ChaosThread[] cThreads = new ChaosThread[cores];
		int threadIters = iterations/cores;
		for (int i=0;i<cores-1;i++) {
			cThreads[i] = new ChaosThread(histogramHeight,histogramWidth,gamma,threadIters,functions,functionProbabilities,zoomFactor,xOffset,yOffset,supersampleLevel,null);
		}
		cThreads[cores-1] = new ChaosThread(histogramHeight,histogramWidth,gamma,threadIters+(iterations-((iterations/cores)*cores)),functions,functionProbabilities,zoomFactor,xOffset,yOffset,supersampleLevel,progressBar);
		for (int i=0;i<cores;i++) {
			try {
				cThreads[i].getThread().join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int densityMax = 0;
		int[][][] threadColorHistograms = new int[cores][][];
		for (int i=0;i<cores;i++) {
			int[][] threadDensityHistogram = cThreads[i].getDensityHistogram();
			threadColorHistograms[i] = cThreads[i].getColorHistogram();
			for (int r=0;r<histogramHeight;r++) {
				for (int c=0;c<histogramWidth;c++) {
					densityHistogram[r][c] += threadDensityHistogram[r][c];
				}
			}
			densityMax = densityMax + cThreads[i].getDensityMax();
		}
		//System.out.println(threadColorHistograms[0][0].length);
		for (int r=0;r<histogramHeight;r++) {
			for (int c=0;c<histogramWidth;c++) {
				int[] coreColors = new int[cores];
				for (int i=0;i<cores;i++) {
					coreColors[i] = threadColorHistograms[i][r][c];
				}
				colorHistogram[r][c] = RunnableChaosGame.blendColors(coreColors);
			}
		}
//		System.out.println("functions[0].getConstantA(): "+functions[0].getConstantA());
//		while (iterations-- > 0) {
//			if (ended)
//				return;
//			else {
//				//System.out.println("lastX: "+lastX);
//				Point resultingPoint;
//				IteratedFunction f = null;
//				//Pick a function.
//				double randomValue = Math.random();
//				double lastProbability = 0;
//				int functionNumber=0;
//				
//				while ((functionNumber<functions.length) && (f==null)) {
//					if (randomValue>=lastProbability && randomValue<(lastProbability+functionProbabilities[functionNumber])) {
//						f = functions[functionNumber];
//					}
//					lastProbability+=functionProbabilities[functionNumber];
//					functionNumber++;
//				}
//				resultingPoint = f.calculate(lastX, lastY);
////				if (Double.isNaN(resultingPoint.getX()) ) {
////					System.out.println("a "+f.getConstantA());
////					System.out.println("b "+f.getConstantB());
////					System.out.println("c "+f.getConstantC());
////					System.out.println("d "+f.getConstantD());
////					System.out.println("e "+f.getConstantE());
////					System.out.println("f "+f.getConstantF());
////					double[] weights = f.getVariationWeights();
////					for (int w=0;w<weights.length;w++) {
////						System.out.print("w["+w+"]: "+weights[w]);
////					}
////					JOptionPane.showMessageDialog(null,"lastX is NaN from f("+lastX+","+lastY+") on iteration " + iterations + ". See output.");
////					
////				}
//				lastX = resultingPoint.getX();
//				
//				lastY = resultingPoint.getY();
//				int pixelX = (int)((lastX*zoomFactor+xOffset)*supersampleLevel);
//				int pixelY = (int)((lastY*zoomFactor+yOffset)*supersampleLevel);
////				System.out.println("pixelX: "+pixelX);
////				System.out.println("const a: "+f.getConstantA());
//				currentColor = RunnableChaosGame.blendColors(f.getColor(),currentColor);
//				if ((pixelX < histogramWidth) && (pixelY < histogramHeight) && (pixelX >= 0) && (pixelY >= 0)) {
//					densityHistogram[pixelY][pixelX]+=1;
//					if (densityMax < densityHistogram[pixelY][pixelX])
//						densityMax = densityHistogram[pixelY][pixelX];
//					colorHistogram[pixelY][pixelX] = RunnableChaosGame.blendColors(colorHistogram[pixelY][pixelX],currentColor);
//				}
//				progressBar.setValue(progressBar.getValue()+1);
//			}
//		}
		progressBar.setValue(0);
		progressBar.setMaximum(img.getHeight()*img.getWidth());
		progressBar.setString("Updating image...");
		boolean goodFrame = true;
		double brightness = 0;
		for (int imgRow=0;imgRow<img.getHeight();imgRow++) {
			for (int imgCol=0;imgCol<img.getWidth();imgCol++) {
				if (ended)
					return;
				else {
					double density = getSupersampledDensity(densityHistogram,imgCol,imgRow);
					double alpha;
					if (density == 0)
						alpha = 0;
					else
						alpha = Math.log(density)/Math.log(densityMax);
					int supersampledColor = getSupersampledColor(colorHistogram,imgCol,imgRow);
					double gamma_result = Math.pow(alpha,inverse_gamma);
					int green_color = (int)(ImageUtilities.getGreenComponent(supersampledColor)*gamma_result);
					int blue_color = (int)(ImageUtilities.getBlueComponent(supersampledColor)*gamma_result);
					int red_color = (int)(ImageUtilities.getRedComponent(supersampledColor)*gamma_result);
					int resultingColor = ImageUtilities.combine(ImageUtilities.MAX_COLOR,(int)(ImageUtilities.getRedComponent(supersampledColor)*gamma_result),(int)(ImageUtilities.getGreenComponent(supersampledColor)*gamma_result),(int)(ImageUtilities.getBlueComponent(supersampledColor)*gamma_result));
					brightness = brightness + Math.sqrt((red_color*red_color)+(green_color*green_color)+(blue_color*blue_color));
					img.setRGB(imgCol, imgRow, resultingColor);
					progressBar.setValue(progressBar.getValue()+1);
				}
			}
		}
		brightness = brightness/(img.getWidth()*img.getHeight()*441.6729559);
		if (brightness<0.005) {
			goodFrame = false;
			System.out.println("bad frame");
		}
		//System.out.println(displayer.getAnimationFrames());
		if (displayer.getAnimationFrames()>=0) {
			displayer.continueAnimation(this,!goodFrame);
		}
	}
	@Override
	public void run() {
		chaosGame();
		displayer.redrawImage();
	}

}
