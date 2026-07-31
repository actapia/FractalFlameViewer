package edu.uky.cs.acta225.flame;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

import edu.uky.cs.acta225.imageutils.ImageUtilities;

public class RunnableChaosGame extends ProgressProcess implements Runnable, ProgressListener {
//	FlameDisplay displayer;
	private double zoomFactor, gamma;
	private int xOffset, yOffset, iterations, supersampleLevel;
	private BufferedImage img;
//	IteratedFunction[] functions;
	private boolean ended;
	private Distribution<IteratedFunction> functionProbabilities;
	private ArrayList<RenderListener> renderListeners;
	
	public static Distribution<IteratedFunction> deepCloneDistribution(Distribution<IteratedFunction> probs) {
		Distribution<IteratedFunction> newProbs = new Distribution<IteratedFunction>();
		for (var key: probs.keySet()) {
			newProbs.put(key.deepClone(), probs.get(key));
		}
		return newProbs;
	}
	
	public RunnableChaosGame(Distribution<IteratedFunction> probabilities, int width, int height, int iter, double zoom, double g, int x, int y, int ssLevel) {
		super();
//		displayer = disp;
//		progressBar = prog;
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		iterations = iter;
		zoomFactor = zoom;
		gamma = g;
		xOffset = x;
		yOffset = y;
		supersampleLevel = ssLevel;
		ended = false;
		functionProbabilities = probabilities;
		renderListeners = new ArrayList<RenderListener>();
	}
	
	public BufferedImage getImage() {
		return img;
	}
	
	
	public void addRenderListener(RenderListener listener) {
		renderListeners.add(listener);
	}
	
	private void finishRender() {
		RenderEvent event = new RenderEvent(this);
		for (RenderListener listener: renderListeners) {
			listener.renderFinished(event);
		}
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
		progress(0, iterations, "Iterating...");
//		progressBar.setValue(0);
//		progressBar.setMaximum(iterations);
//		progressBar.setString("Iterating...");
		int currentColor = ImageUtilities.combine(ImageUtilities.MAX_COLOR,(int)(Math.random()*ImageUtilities.MAX_COLOR),(int)(Math.random()*ImageUtilities.MAX_COLOR),(int)(Math.random()*ImageUtilities.MAX_COLOR));;
		int cores = Runtime.getRuntime().availableProcessors();
		ChaosThread[] cThreads = new ChaosThread[cores];
		int threadIters = iterations/cores;
		for (int i=0;i<cores-1;i++) {
			cThreads[i] = new ChaosThread(histogramHeight,histogramWidth,gamma,threadIters,functionProbabilities,zoomFactor,xOffset,yOffset,supersampleLevel);
		}
		cThreads[cores-1] = new ChaosThread(histogramHeight,histogramWidth,gamma,threadIters+(iterations-((iterations/cores)*cores)),functionProbabilities,zoomFactor,xOffset,yOffset,supersampleLevel);
		cThreads[cores-1].addProgressListener(this);
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
		
		for (int r=0;r<histogramHeight;r++) {
			for (int c=0;c<histogramWidth;c++) {
				int[] coreColors = new int[cores];
				for (int i=0;i<cores;i++) {
					coreColors[i] = threadColorHistograms[i][r][c];
				}
				colorHistogram[r][c] = RunnableChaosGame.blendColors(coreColors);
			}
		}

		progress(0, img.getHeight()*img.getWidth(), "Updating image...");
		boolean goodFrame = true;
		double brightness = 0;
		int progressValue = 0;
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
					progress(progressValue++);
				}
			}
		}
		brightness = brightness/(img.getWidth()*img.getHeight()*441.6729559);
		if (brightness<0.005) {
			goodFrame = false;
			System.out.println("bad frame");
		}
		//System.out.println(displayer.getAnimationFrames());
//		if (displayer.getAnimationFrames()>=0) {
//			displayer.continueAnimation(this,!goodFrame);
//		}
	}
	@Override
	public void run() {
		chaosGame();
		finishRender();
	}

	@Override
	public void progressUpdated(ProgressEvent event) {
		for (ProgressListener listener: progressListeners) {
			listener.progressUpdated(event);
		}
	}

}
