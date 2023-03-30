import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import Other.ImageUtilities;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FlameDisplay extends JFrame implements ActionListener,MouseWheelListener,ChangeListener,MouseMotionListener,MouseListener {
	private ImagePanel fractalImagePanel;
	private JSpinner xSpinner,ySpinner,zoomSpinner; //These are the position spinners.
	private JSpinner heightSpinner, widthSpinner; //These are the size spinners;
	private JSpinner gammaSpinner, supersampleSpinner, samplesSpinner; //These are the display spinners.
	private JSpinner numFunctionsSpinner; //This is the function spinner.
	private JSpinner framesSpinner; //This spinner controls the number of frames for an animation.
	private JSpinner[] variationSpinners; //These are the spinners that control the weights of each variation.
	private JButton centerButton, zeroButton;
	private JButton randomizeProbabilitiesButton;
	private JButton newFractalButton;
	private JButton animateButton, browseButton, cancelButton;
	private JTextField outputField;
	private JProgressBar imageProgress;
	private JComboBox<String> functionsComboBox;
	private IteratedFunction[] currentFunctions;
	private JPanel functionCards;
	private JPanel optionsPanel;
	private JMenuItem saveItem;
	private int lastMouseX, lastMouseY;
	private RunnableChaosGame lastChaosGame;
	private JCheckBox animateConstantsBox, animateVariationWeightsBox, animateFunctionProbabilitiesBox, animateFlameColorBox, multiAnimationBox;
	
	private double[] functionProbabilities;
	private static double[] initialVariationWeightLimits = new double[IteratedFunction.NUMBER_OF_VARIATIONS];
	private int frames,fractalNumber;
	private boolean cancelled;
	
	private Animator aConstantAnimator,bConstantAnimator,cConstantAnimator,dConstantAnimator,eConstantAnimator,fConstantAnimator;
	private Animator[] variationWeightAnimators;
	private Animator[] functionProbabilityAnimators;
	private Animator[][] flameColorAnimators;
	
	private static final int HUE_INDEX = 0;
	private static final int SATURATION_INDEX = 1;
	private static final int VALUE_INDEX = 2;
	
	private File outputDirectory;
	
	
	protected int mod(int n,int d) { //This is my "improved" modulus function. It always returns the congruence class of n mod d between 0 and (d-1), inclusive.
		int res = n%d;
		if (res<0)
			res = d+res;
		return res;
	}
	
	private void chaosGame(BufferedImage img,int iterations, IteratedFunction[] functions) {
//		System.out.println("Running chaos game: functions[0].getConstantA() = "+functions[0].getConstantA()); 
//		for (int f=0;f<functions.length;f++) {
//			double[] weights = functions[f].getVariationWeights();
//			for (int w=0;w<weights.length;w++) {
//				System.out.print("w["+w+"] = "+weights[w]+"\t");
//			}
//			System.out.println();
//		}
		if (lastChaosGame != null) {
			lastChaosGame.end();
		}
		RunnableChaosGame cg = new RunnableChaosGame(this,
			imageProgress,
			img,
			iterations,functions,
			((Double)zoomSpinner.getValue()).doubleValue(),
			((Double)gammaSpinner.getValue()).doubleValue(),
			((Integer)xSpinner.getValue()).intValue(),
			((Integer)ySpinner.getValue()).intValue(),
			((Integer)supersampleSpinner.getValue()).intValue(),
			functionProbabilities);
		lastChaosGame = cg;	
		(new Thread(cg)).start();
	}
	
	public void continueAnimation(RunnableChaosGame verified, boolean badFrame) {
		if (verified==lastChaosGame) {
			if (cancelled) {
				enableEverything(optionsPanel);
				cancelButton.setEnabled(false);
			}
			else {
				if (frames == 0) {
					if (multiAnimationBox.isSelected() && cancelButton.isEnabled()) {
						System.out.println("new fractal");
						frames = ((Integer)framesSpinner.getValue()).intValue();
						newFractal();
						disableEverything(optionsPanel);
						cancelButton.setEnabled(true);
						fractalNumber++;
						animate();
					}
					else {
						enableEverything(optionsPanel);
						cancelButton.setEnabled(false);
					}
				}
				else {
					if (badFrame) {
						frames++;
						boolean animateConstants = animateConstantsBox.isSelected();
						boolean animateVariationWeights = animateVariationWeightsBox.isSelected();
						boolean animateFunctionProbabilities = animateFunctionProbabilitiesBox.isSelected();
						boolean animateFlameColors = animateFlameColorBox.isSelected();
						if (animateVariationWeights) {
							for (Animator ani: variationWeightAnimators) {
								ani.reverse();
							}
						}
						if (animateConstants) {
							aConstantAnimator.reverse();
							bConstantAnimator.reverse();
							cConstantAnimator.reverse();
							dConstantAnimator.reverse();
							eConstantAnimator.reverse();
							fConstantAnimator.reverse();
						}
						if (animateFunctionProbabilities) {
							for (Animator ani: functionProbabilityAnimators) {
								ani.reverse();
							}
						}
					}
					continueAnimation();
				}
			}
		}
	}
	
	private void continueAnimation() {
		//Save current image.
//		try {
		    BufferedImage bi = fractalImagePanel.getImage(); //Get the image from the fractal panel.
//		    File file = new File(outputDirectory.getAbsolutePath()+"/flame_"+fractalNumber+"_frame"+(((Integer)framesSpinner.getValue()).intValue()-frames)+".png");
//		    ImageIO.write(bi, "png", file); //Write it.
		    if (frames>0) {
				boolean animateConstants = animateConstantsBox.isSelected();
				boolean animateVariationWeights = animateVariationWeightsBox.isSelected();
				boolean animateFunctionProbabilities = animateFunctionProbabilitiesBox.isSelected();
				boolean animateFlameColors = animateFlameColorBox.isSelected();
				if (animateConstants) {
					for (int functionIndex=0;functionIndex<currentFunctions.length;functionIndex++) {
						double a = aConstantAnimator.animate(currentFunctions[functionIndex].getConstantA());
						double b = bConstantAnimator.animate(currentFunctions[functionIndex].getConstantB());
						double c = cConstantAnimator.animate(currentFunctions[functionIndex].getConstantC());
						double d = dConstantAnimator.animate(currentFunctions[functionIndex].getConstantD());
						double e = eConstantAnimator.animate(currentFunctions[functionIndex].getConstantE());
						double f = fConstantAnimator.animate(currentFunctions[functionIndex].getConstantF());
						//System.out.println(currentFunctions[functionIndex].getVariationWeights());
						currentFunctions[functionIndex] = new IteratedFunction(a,b,c,d,e,f,currentFunctions[functionIndex].getVariationWeights(),currentFunctions[functionIndex].getColor());
					}
				}
				if (animateVariationWeights) {
					for (int functionIndex=0;functionIndex<currentFunctions.length;functionIndex++) {
						double[] weights = currentFunctions[functionIndex].getVariationWeights();
						for (int weightIndex=0;weightIndex<weights.length;weightIndex++) {
							weights[weightIndex] = variationWeightAnimators[weightIndex].animate(weights[weightIndex]);
						}
						currentFunctions[functionIndex] = new IteratedFunction(currentFunctions[functionIndex].getConstantA(),
								currentFunctions[functionIndex].getConstantB(),currentFunctions[functionIndex].getConstantC(),
								currentFunctions[functionIndex].getConstantD(),currentFunctions[functionIndex].getConstantE(),
								currentFunctions[functionIndex].getConstantF(),weights,currentFunctions[functionIndex].getColor());
					}
				}
				if (animateFunctionProbabilities) {
					double[] originalProbabilities = functionProbabilities.clone();
					for (int probabilityIndex=0;probabilityIndex<functionProbabilities.length;probabilityIndex++) {
						functionProbabilities[probabilityIndex] = Math.max(0,functionProbabilityAnimators[probabilityIndex].animate(functionProbabilities[probabilityIndex]));
					}
					double totalFunctionProbabilities = 0;
					for (int i=0;i<currentFunctions.length;i++) {
						functionProbabilities[i] = Math.random();
						totalFunctionProbabilities += functionProbabilities[i];
					}
					if (totalFunctionProbabilities == 0) {
						functionProbabilities = originalProbabilities;
					}
					for (int i=0;i<currentFunctions.length;i++) {
						functionProbabilities[i] = functionProbabilities[i]/totalFunctionProbabilities;
					}	
				}
				if (animateFlameColors) {
					for (int functionIndex=0;functionIndex<currentFunctions.length;functionIndex++) {
						int color = currentFunctions[functionIndex].getColor();
						float[] hsbValues = Color.RGBtoHSB(ImageUtilities.getRedComponent(color), ImageUtilities.getGreenComponent(color), ImageUtilities.getBlueComponent(color), null);
//						System.out.println("original saturation: "+hsbValues[SATURATION_INDEX]);
						double hue = flameColorAnimators[functionIndex][HUE_INDEX].animate(hsbValues[HUE_INDEX]);
						double value = Math.max(0,Math.min(1,flameColorAnimators[functionIndex][VALUE_INDEX].animate(hsbValues[VALUE_INDEX])));
						double saturation = Math.max(0,Math.min(1,flameColorAnimators[functionIndex][SATURATION_INDEX].animate(hsbValues[SATURATION_INDEX])));
//						System.out.println("h: "+hue+"\ts: "+saturation+"\tb: "+value);
						color = Color.HSBtoRGB((float)hue, (float)saturation, (float)value);
						currentFunctions[functionIndex].setColor(color);
					}
				}
				frames--;
				updateImage();
			}
//		} catch (IOException error) {
//			//Something bad happened.
//		    JOptionPane.showMessageDialog(this, "Error in saving file!", "Oh child!", JOptionPane.WARNING_MESSAGE);
//		}
		
	}
	
	private void animate() {
		final int HSV_LENGTH = 3;
		aConstantAnimator = null;
		bConstantAnimator = null;
		cConstantAnimator = null;
		dConstantAnimator = null;
		eConstantAnimator = null;
		fConstantAnimator = null;
		cancelled = false;
		variationWeightAnimators = new Animator[IteratedFunction.NUMBER_OF_VARIATIONS];
		functionProbabilityAnimators = new Animator[currentFunctions.length];
		flameColorAnimators = new Animator[currentFunctions.length][HSV_LENGTH];
		boolean animateConstants = animateConstantsBox.isSelected();
		boolean animateVariationWeights = animateVariationWeightsBox.isSelected();
		boolean animateFunctionProbabilities = animateFunctionProbabilitiesBox.isSelected();
		boolean animateFlameColors = animateFlameColorBox.isSelected();
		if (animateConstants) {
			aConstantAnimator = new Animator();
			bConstantAnimator = new Animator();
			cConstantAnimator = new Animator();
			dConstantAnimator = new Animator();
			eConstantAnimator = new Animator();
			fConstantAnimator = new Animator();
		}
		if (animateVariationWeights) {
			for (int animatorIndex=0;animatorIndex<variationWeightAnimators.length;animatorIndex++) {
				variationWeightAnimators[animatorIndex] = new Animator();
			}
		}
		if (animateFunctionProbabilities) {
			for (int animatorIndex=0;animatorIndex<functionProbabilityAnimators.length;animatorIndex++) {
				double MAX_PROBABILITY_ACCELERATION = 0.00000005; //Iterated Function Systems are extremely sensitive to the probability.
				functionProbabilityAnimators[animatorIndex] = new Animator(0,Math.random()*MAX_PROBABILITY_ACCELERATION);
			}
		}
		if (animateFlameColors) {
			final double INITIAL_COLOR_ACCELERATION = 0.0003;
			final double MAX_INITIAL_COLOR_VELOCITY = 0.003;
			for (int animatorIndex=0;animatorIndex<flameColorAnimators.length;animatorIndex++) {
					flameColorAnimators[animatorIndex][HUE_INDEX] = new Animator(Math.random()*(INITIAL_COLOR_ACCELERATION/2.0)-INITIAL_COLOR_ACCELERATION,Math.random()*(MAX_INITIAL_COLOR_VELOCITY/2.0)-MAX_INITIAL_COLOR_VELOCITY);
					flameColorAnimators[animatorIndex][SATURATION_INDEX] = new Animator();
					flameColorAnimators[animatorIndex][VALUE_INDEX]  = new Animator();
			}
		}
		continueAnimation();
	}
	
	public int getAnimationFrames() {
		return frames;
	}
	

	
//	private void old_chaosGame(BufferedImage img,int iterations, IteratedFunction[] functions) {
//		final int N_GON = 5;
//		double lastX = (int)(Math.random()*img.getWidth());
//		double lastY = (int)(Math.random()*img.getHeight());
//		img.setRGB((int)lastX, (int)lastY, ImageUtilities.combine(255,255,255,255));
//		//Choose a random vertex.
//		int lastVertex = -1;
//		Point[] vertices = new Point[N_GON];
//		for (int i=0;i<vertices.length;i++) {
//			double angle = i*2*(Math.PI/vertices.length);
//			vertices[i] = new Point();
//			vertices[i].setLocation((img.getWidth()*(Math.sin(angle)+1))/2,(img.getHeight()*(Math.cos(angle)+1))/2);
//		}
//		while (iterations-- > 0) {
//			Point resultingPoint;
//			IteratedFunction f = functions[(int)(Math.random()*functions.length)];
//			resultingPoint = f.calculate(lastX, lastY);
//			lastX = resultingPoint.getX();
//			lastY = resultingPoint.getY();
//			int pixelX = (int)(lastX*50);
//			int pixelY = (int)(lastY*50);
//			if ((pixelX < img.getWidth()) && (pixelY < img.getHeight()) && (pixelX >= 0) && (pixelY >= 0))
//				img.setRGB(pixelX, pixelY, ImageUtilities.combine(255,255,255,255));
//		}
//	}

	public FlameDisplay() {
		super();
		final int INITIAL_FRACTAL_WIDTH = 500;
		final int INITIAL_FRACTAL_HEIGHT = 500;
		final int INITIAL_ZOOM = 50;
		final double ZOOM_STEP = 0.1;
		final double GAMMA_STEP = 0.1;
		final double VARIATION_STEP = 0.1;
		final int FRAME_STEP = 1;
		final int INITIAL_ITERATIONS = 1000000;
		final int INITIAL_FRAME_WIDTH = 1400;
		final int INITIAL_FRAME_HEIGHT = 600;
		final double INITIAL_GAMMA = 1;
		final int INITIAL_SUPERSAMPLE_LEVELS = 1;
		final int INITIAL_FUNCTIONS = 5;
		//Setup the variation weights.
		initialVariationWeightLimits[IteratedFunction.VARIATION_HORSESHOE] = 0;
		initialVariationWeightLimits[IteratedFunction.VARIATION_LINEAR] = 1; //1
		initialVariationWeightLimits[IteratedFunction.VARIATION_POPCORN] = 1; 
		initialVariationWeightLimits[IteratedFunction.VARIATION_SINUSOIDAL] = 1; //1
		initialVariationWeightLimits[IteratedFunction.VARIATION_SPHERICAL] = 1; //1
		initialVariationWeightLimits[IteratedFunction.VARIATION_SWIRL] = 1; //1
		//Create the fractal image.
		BufferedImage fractalImage = new BufferedImage(INITIAL_FRACTAL_WIDTH,INITIAL_FRACTAL_HEIGHT,BufferedImage.TYPE_INT_RGB);
		fractalImagePanel = new ImagePanel(fractalImage);
		JScrollPane fractalScroller = new JScrollPane(fractalImagePanel);
		JPanel rightSide = new JPanel(new BorderLayout());
		//Create the menu bar.
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File"); //This is for file saving operations, and miscellaneous things that don't affect the image. 
		saveItem = new JMenuItem("Save...");
		//Create necessary components for the options panel.
		optionsPanel = new JPanel();
		imageProgress = new JProgressBar(0,INITIAL_ITERATIONS);
		//Create the components for the display (position) panel.
		JPanel positionPanel = new JPanel();
		JLabel xPositionLabel = new JLabel("X Offset:");
		JLabel yPositionLabel = new JLabel("Y Offset:");
		JLabel zoomLabel = new JLabel("Zoom:");
		JPanel xPositionPanel = new JPanel();
		JPanel yPositionPanel = new JPanel();
		JPanel positionButtonsPanel = new JPanel();
		JPanel zoomPanel = new JPanel();
		SpinnerNumberModel xPositionModel = new SpinnerNumberModel(INITIAL_FRACTAL_WIDTH/2,Integer.MIN_VALUE,Integer.MAX_VALUE,1);
		SpinnerNumberModel yPositionModel = new SpinnerNumberModel(INITIAL_FRACTAL_HEIGHT/2,Integer.MIN_VALUE,Integer.MAX_VALUE,1);
		xSpinner = new JSpinner(xPositionModel);
		ySpinner = new JSpinner(yPositionModel);
		SpinnerNumberModel zoomModel = new SpinnerNumberModel(INITIAL_ZOOM,0,Double.MAX_VALUE,ZOOM_STEP);
		zoomSpinner = new JSpinner(zoomModel);
		centerButton = new JButton("Center fractal");
		zeroButton = new JButton("Zero position");
		//Create the components for the size panel.
		JPanel sizePanel = new JPanel();
		JLabel widthLabel = new JLabel("Image width:");
		JLabel heightLabel = new JLabel("Image height:");
		JPanel widthPanel = new JPanel();
		JPanel heightPanel = new JPanel();
		SpinnerNumberModel widthModel = new SpinnerNumberModel(INITIAL_FRACTAL_WIDTH,1,Integer.MAX_VALUE,1);
		SpinnerNumberModel heightModel = new SpinnerNumberModel(INITIAL_FRACTAL_HEIGHT,1,Integer.MAX_VALUE,1);
		widthSpinner = new JSpinner(widthModel);
		heightSpinner = new JSpinner(heightModel);
		//Create the components for the display panel.
		JPanel displayPanel = new JPanel();
		JLabel gammaLabel = new JLabel("Gamma:");
		JLabel supersampleLabel = new JLabel("Supersample levels:");
		JLabel samplesLabel = new JLabel("Samples:");
		JPanel gammaPanel = new JPanel();
		JPanel supersamplePanel = new JPanel();
		JPanel samplesPanel = new JPanel();
		SpinnerNumberModel gammaModel = new SpinnerNumberModel(INITIAL_GAMMA,1,Double.MAX_VALUE,GAMMA_STEP);
		SpinnerNumberModel supersampleModel = new SpinnerNumberModel(INITIAL_SUPERSAMPLE_LEVELS,1,Integer.MAX_VALUE,1);
		SpinnerNumberModel samplesModel = new SpinnerNumberModel(INITIAL_ITERATIONS,1,Integer.MAX_VALUE,1);
		gammaSpinner = new JSpinner(gammaModel);
		supersampleSpinner = new JSpinner(supersampleModel);
		samplesSpinner = new JSpinner(samplesModel);
		//Create the components for the function panel.
		JPanel functionPanel = new JPanel();
		JLabel functionsLabel = new JLabel("Functions:");
		JPanel functionsPanel = new JPanel();
		SpinnerNumberModel functionsModel = new SpinnerNumberModel(INITIAL_FUNCTIONS,0,Integer.MAX_VALUE,1);
		numFunctionsSpinner = new JSpinner(functionsModel);
		functionsComboBox = new JComboBox<String>(); //The items will be setup later when the functions are created.
		functionCards = new JPanel();
		randomizeProbabilitiesButton = new JButton("Randomize probabilities");
		//Create the components for the variations panel.
		JPanel variationsPanel = new JPanel();
		JLabel[] variationLabels = new JLabel[IteratedFunction.NUMBER_OF_VARIATIONS];
		variationLabels[IteratedFunction.VARIATION_HORSESHOE] = new JLabel("Horseshoe:");
		variationLabels[IteratedFunction.VARIATION_LINEAR] = new JLabel("Linear:");
		variationLabels[IteratedFunction.VARIATION_POPCORN] = new JLabel("Popcorn:");
		variationLabels[IteratedFunction.VARIATION_SINUSOIDAL] = new JLabel("Sinusoidal:");
		variationLabels[IteratedFunction.VARIATION_SPHERICAL] = new JLabel("Spherical:");
		variationLabels[IteratedFunction.VARIATION_SWIRL] = new JLabel("Swirl:");
		variationLabels[IteratedFunction.VARIATION_EYEFISH] = new JLabel("Eyefish:");
		variationSpinners = new JSpinner[IteratedFunction.NUMBER_OF_VARIATIONS];
		JPanel[] variationPanels = new JPanel[IteratedFunction.NUMBER_OF_VARIATIONS];
		for (int variationNumber=0;variationNumber<IteratedFunction.NUMBER_OF_VARIATIONS;variationNumber++) {
			SpinnerNumberModel variationsModel = new SpinnerNumberModel(initialVariationWeightLimits[variationNumber],0,Double.MAX_VALUE,VARIATION_STEP);
			variationSpinners[variationNumber] = new JSpinner(variationsModel);
			variationPanels[variationNumber] = new JPanel();
		}
		newFractalButton = new JButton("New fractal");
		JSplitPane imageOptionsSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,fractalScroller,rightSide);
		//Create the components for the animation panel.
		JPanel animationPanel = new JPanel();
		JPanel animateConstantsPanel = new JPanel();
		JPanel animateVariationWeightsPanel = new JPanel();
		JPanel animateFunctionProbabilitiesPanel = new JPanel();
		JPanel animateFlameColorsPanel = new JPanel();
		JPanel framesPanel = new JPanel();
		JPanel directoryPanel = new JPanel();
		JPanel multiPanel = new JPanel();
		JPanel animateCancelPanel = new JPanel();
		JLabel animateConstantsLabel = new JLabel("Function constants:");
		JLabel animateVariationWeightsLabel = new JLabel("Variation weights:");
		JLabel animateFunctionProbabilitiesLabel = new JLabel("Function Probabilities:");
		JLabel animateFlameColorLabel = new JLabel("Flame colors:");
		JLabel framesLabel = new JLabel("Frames:");
		JLabel outputLabel = new JLabel("Output directory:");
		JLabel multiLabel = new JLabel("New animation when finished:");
		animateConstantsBox  = new JCheckBox();
		animateVariationWeightsBox = new JCheckBox();
		animateFunctionProbabilitiesBox = new JCheckBox();
		animateFlameColorBox = new JCheckBox();
		multiAnimationBox = new JCheckBox();
		SpinnerNumberModel framesModel = new SpinnerNumberModel(1,1,Integer.MAX_VALUE,FRAME_STEP);
		framesSpinner = new JSpinner(framesModel);
		cancelButton = new JButton("Cancel");
		animateButton = new JButton("Animate!");
		outputField = new JTextField();
		browseButton = new JButton("Browse...");
		//Set options for components.
		//Set options for spinners.
		final int SPINNER_HEIGHT = 20;
		final int SPINNER_WIDTH = 300;
		Dimension spinnerDimensions = new Dimension(SPINNER_WIDTH,SPINNER_HEIGHT);
		xPositionPanel.setPreferredSize(spinnerDimensions);
		yPositionPanel.setPreferredSize(spinnerDimensions);
		zoomPanel.setPreferredSize(spinnerDimensions);
		widthPanel.setPreferredSize(spinnerDimensions);
		heightPanel.setPreferredSize(spinnerDimensions);
		gammaPanel.setPreferredSize(spinnerDimensions);
		supersamplePanel.setPreferredSize(spinnerDimensions);
		samplesPanel.setPreferredSize(spinnerDimensions);
		positionButtonsPanel.setPreferredSize(spinnerDimensions);
		functionsPanel.setPreferredSize(spinnerDimensions);
		final int OPTION_HEIGHT = 20;
		final int OPTION_WIDTH = 10;
		Dimension optionDimensions = new Dimension(OPTION_WIDTH,OPTION_HEIGHT);
		animateConstantsPanel.setPreferredSize(optionDimensions);
		animateFunctionProbabilitiesPanel.setPreferredSize(optionDimensions);
		animateVariationWeightsPanel.setPreferredSize(optionDimensions);
		animateFlameColorsPanel.setPreferredSize(optionDimensions);
		final int OUTPUT_FIELD_HEIGHT = 20;
		final int OUTPUT_FIELD_WIDTH = 175;
		outputField.setPreferredSize(new Dimension(OUTPUT_FIELD_WIDTH,OUTPUT_FIELD_HEIGHT));
		framesPanel.setPreferredSize(spinnerDimensions);
		for (int variationNumber=0;variationNumber<IteratedFunction.NUMBER_OF_VARIATIONS;variationNumber++) {
			variationPanels[variationNumber].setPreferredSize(spinnerDimensions);
		}
		//Set options for the progress bar.
		final int PROGRESS_BAR_HEIGHT = 25;
		final int PROGRESS_BAR_WIDTH = 30;
		imageProgress.setPreferredSize(new Dimension(PROGRESS_BAR_WIDTH,PROGRESS_BAR_HEIGHT));
		imageProgress.setStringPainted(true);
		//Setup layouts.
		final int INITIAL_DIVIDER_LOCATION = 550;
		final int MINIMUM_OPTIONS_PANEL_WIDTH = 200;
		final int NUM_COLUMNS = 2;
		final int NUM_ROWS = 1;
		positionPanel.setLayout(new BoxLayout(positionPanel,BoxLayout.Y_AXIS));
		sizePanel.setLayout(new BoxLayout(sizePanel,BoxLayout.Y_AXIS));
		displayPanel.setLayout(new BoxLayout(displayPanel,BoxLayout.Y_AXIS));
		functionPanel.setLayout(new BoxLayout(functionPanel,BoxLayout.Y_AXIS));
		variationsPanel.setLayout(new BoxLayout(variationsPanel,BoxLayout.Y_AXIS));
		animationPanel.setLayout(new BoxLayout(animationPanel,BoxLayout.Y_AXIS));
		functionCards.setLayout(new CardLayout());
		xPositionPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		yPositionPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		zoomPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		heightPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		widthPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		gammaPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		supersamplePanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		samplesPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		positionButtonsPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		positionButtonsPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		functionsPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		animateConstantsPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		animateFunctionProbabilitiesPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		animateFlameColorsPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		animateVariationWeightsPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		framesPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		multiPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		for (int variationNumber=0;variationNumber<IteratedFunction.NUMBER_OF_VARIATIONS;variationNumber++) {
			variationPanels[variationNumber].setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS));
		}
		animateConstantsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		animateConstantsBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		animateConstantsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		animateFunctionProbabilitiesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		animateFunctionProbabilitiesBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		animateFunctionProbabilitiesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		animateFlameColorBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		animateFlameColorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		animateFlameColorsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		animateVariationWeightsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		animateVariationWeightsBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		animateVariationWeightsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		framesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		framesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		directoryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		outputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		outputField.setAlignmentX(Component.CENTER_ALIGNMENT);
		browseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		animateCancelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		randomizeProbabilitiesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		positionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Position"),BorderFactory.createEmptyBorder(10,10,10,10)));
		sizePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Size"),BorderFactory.createEmptyBorder(10,10,10,10)));
		displayPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Display"),BorderFactory.createEmptyBorder(10,10,10,10)));
		functionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Function"),BorderFactory.createEmptyBorder(10,10,10,10)));
		variationsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Variations"),BorderFactory.createEmptyBorder(10,10,10,10)));
		animationPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Animation"),BorderFactory.createEmptyBorder(10,10,10,10)));
		optionsPanel.setMinimumSize(new Dimension(MINIMUM_OPTIONS_PANEL_WIDTH,INITIAL_FRAME_HEIGHT));
		imageOptionsSplitter.setDividerLocation(INITIAL_DIVIDER_LOCATION);
		imageOptionsSplitter.setOneTouchExpandable(true);
		//Add components to their respective parents.
		xPositionPanel.add(xPositionLabel);
		xPositionPanel.add(xSpinner);
		yPositionPanel.add(yPositionLabel);
		yPositionPanel.add(ySpinner);
		positionButtonsPanel.add(centerButton);
		positionButtonsPanel.add(zeroButton);
		zoomPanel.add(zoomLabel);
		zoomPanel.add(zoomSpinner);
		widthPanel.add(widthLabel);
		widthPanel.add(widthSpinner);
		heightPanel.add(heightLabel);
		heightPanel.add(heightSpinner);
		gammaPanel.add(gammaLabel);
		gammaPanel.add(gammaSpinner);
		supersamplePanel.add(supersampleLabel);
		supersamplePanel.add(supersampleSpinner);
		samplesPanel.add(samplesLabel);
		samplesPanel.add(samplesSpinner);
		functionsPanel.add(functionsLabel);
		functionsPanel.add(numFunctionsSpinner);
		for (int variationNumber=0;variationNumber<IteratedFunction.NUMBER_OF_VARIATIONS;variationNumber++) {
			variationPanels[variationNumber].add(variationLabels[variationNumber]);
			variationPanels[variationNumber].add(variationSpinners[variationNumber]);
		}
		animateConstantsPanel.add(animateConstantsLabel);
		animateConstantsPanel.add(animateConstantsBox);
		animateVariationWeightsPanel.add(animateVariationWeightsLabel);
		animateVariationWeightsPanel.add(animateVariationWeightsBox);
		animateFunctionProbabilitiesPanel.add(animateFunctionProbabilitiesLabel);
		animateFunctionProbabilitiesPanel.add(animateFunctionProbabilitiesBox);
		animateFlameColorsPanel.add(animateFlameColorLabel);
		animateFlameColorsPanel.add(animateFlameColorBox);
		multiPanel.add(multiLabel);
		multiPanel.add(multiAnimationBox);
		animateCancelPanel.add(cancelButton);
		animateCancelPanel.add(animateButton);
		framesPanel.add(framesLabel);
		framesPanel.add(framesSpinner);
		directoryPanel.add(outputLabel);
		directoryPanel.add(outputField);
		directoryPanel.add(browseButton);
		positionPanel.add(xPositionPanel);
		positionPanel.add(yPositionPanel);
		positionPanel.add(zoomPanel);
		positionPanel.add(positionButtonsPanel);
		sizePanel.add(widthPanel);
		sizePanel.add(heightPanel);
		displayPanel.add(gammaPanel);
		displayPanel.add(supersamplePanel);
		displayPanel.add(samplesPanel);
		functionPanel.add(functionsPanel);
		functionPanel.add(functionsComboBox);
		functionPanel.add(functionCards);
		functionPanel.add(randomizeProbabilitiesButton);
		for (int variationNumber=0;variationNumber<IteratedFunction.NUMBER_OF_VARIATIONS;variationNumber++) {
			variationsPanel.add(variationPanels[variationNumber]);
		}
		variationsPanel.add(newFractalButton);
		animationPanel.add(animateConstantsPanel);
		animationPanel.add(animateVariationWeightsPanel);
		animationPanel.add(animateFunctionProbabilitiesPanel);
		animationPanel.add(animateFlameColorsPanel);
		animationPanel.add(framesPanel);
		animationPanel.add(multiPanel);
		animationPanel.add(directoryPanel);
		animationPanel.add(animateCancelPanel);;
		optionsPanel.add(positionPanel);
		optionsPanel.add(sizePanel);
		optionsPanel.add(displayPanel);
		optionsPanel.add(functionPanel);
		optionsPanel.add(variationsPanel);
		optionsPanel.add(animationPanel);
		rightSide.add(optionsPanel,BorderLayout.CENTER);
		rightSide.add(imageProgress,BorderLayout.SOUTH);
		file.add(saveItem);
		bar.add(file);
		this.add(bar,BorderLayout.NORTH);
		this.add(imageOptionsSplitter);
		//Add the listeners.
		fractalImagePanel.addMouseWheelListener(this);
		fractalImagePanel.addMouseMotionListener(this);
		fractalImagePanel.addMouseListener(this);
		zoomSpinner.addChangeListener(this);
		xSpinner.addChangeListener(this);
		ySpinner.addChangeListener(this);
		widthSpinner.addChangeListener(this);
		heightSpinner.addChangeListener(this);
		samplesSpinner.addChangeListener(this);
		supersampleSpinner.addChangeListener(this);
		gammaSpinner.addChangeListener(this);
		numFunctionsSpinner.addChangeListener(this);
		centerButton.addActionListener(this);
		zeroButton.addActionListener(this);
		randomizeProbabilitiesButton.addActionListener(this);
		newFractalButton.addActionListener(this);
		functionsComboBox.addActionListener(this);
		saveItem.addActionListener(this);
		browseButton.addActionListener(this);
		animateButton.addActionListener(this);
		cancelButton.addActionListener(this);
		setSize(INITIAL_FRAME_WIDTH,INITIAL_FRAME_HEIGHT);
		currentFunctions = new IteratedFunction[INITIAL_FUNCTIONS];
		functionProbabilities = new double[INITIAL_FUNCTIONS];
		double totalFunctionProbabilities = 0;
		for (int i=0;i<currentFunctions.length;i++) {
			String functionString = "Function "+i;
			functionsComboBox.addItem(functionString);
			JPanel ithFunctionPanel = new JPanel();
			functionCards.add(ithFunctionPanel, functionString);
			currentFunctions[i] = new IteratedFunction(initialVariationWeightLimits);
			FunctionColorPanel functionColor = new FunctionColorPanel(currentFunctions[i],this);
			functionProbabilities[i] = Math.random();
			totalFunctionProbabilities += functionProbabilities[i];
			ithFunctionPanel.add(functionColor);
			functionColor.addChangeListener(this);
		}
		//Normalize the probability distribution.
		for (int i=0;i<currentFunctions.length;i++) {
			functionProbabilities[i] = functionProbabilities[i]/totalFunctionProbabilities;
		}
		chaosGame(fractalImage,INITIAL_ITERATIONS,currentFunctions);
		setVisible(true);
		cancelButton.setEnabled(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void updateImage() {
		if (currentFunctions.length>0)
			chaosGame(fractalImagePanel.getImage(),((Integer)samplesSpinner.getValue()).intValue(),currentFunctions);

	}
	
	public void redrawImage() {
		fractalImagePanel.repaint();
		fractalImagePanel.getParent().revalidate();
	}
	
	public void disableEverything(Container comp) {
		for (Component subComponent: comp.getComponents()) {
			subComponent.setEnabled(false);
			if (subComponent instanceof Container) {
				disableEverything((Container)subComponent);
			}
			
		}
	}
	
	public void newFractal() {
		functionCards.removeAll();
		functionsComboBox.removeAllItems();
		int numFunctions = ((Integer)numFunctionsSpinner.getValue()).intValue();
		currentFunctions = new IteratedFunction[numFunctions];
		functionProbabilities = new double[numFunctions];
		double totalFunctionProbabilities = 0;
		double[] variationWeightLimits = new double[IteratedFunction.NUMBER_OF_VARIATIONS];
		for (int variationNumber=0;variationNumber<IteratedFunction.NUMBER_OF_VARIATIONS;variationNumber++) {
			variationWeightLimits[variationNumber] = ((Double)variationSpinners[variationNumber].getValue()).doubleValue();
		}
		for (int i=0;i<currentFunctions.length;i++) {
			String functionString = "Function "+i;
			functionsComboBox.addItem(functionString);
			JPanel ithFunctionPanel = new JPanel();
			functionCards.add(ithFunctionPanel, functionString);
			currentFunctions[i] = new IteratedFunction(variationWeightLimits);
			FunctionColorPanel functionColor = new FunctionColorPanel(currentFunctions[i],this);
			functionProbabilities[i] = Math.random();
			totalFunctionProbabilities += functionProbabilities[i];
			ithFunctionPanel.add(functionColor);
			functionColor.addChangeListener(this);
		}
		//Normalize the probability distribution.
		for (int i=0;i<currentFunctions.length;i++) {
			functionProbabilities[i] = functionProbabilities[i]/totalFunctionProbabilities;
		}
		updateImage();
	}
	
	public void enableEverything(Container comp) {
		
		for (Component subComponent: comp.getComponents()) {
			if (subComponent != cancelButton)
			subComponent.setEnabled(true);
			if (subComponent instanceof Container) {
				enableEverything((Container)subComponent);
			}
			
		}
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		final double MOUSE_WHEEL_SCALE_AMOUNT = 1;
		Double currentZoom = (Double)zoomSpinner.getValue();
		zoomSpinner.setValue(new Double(currentZoom.doubleValue()+MOUSE_WHEEL_SCALE_AMOUNT));
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0) {
		if ((arg0.getSource() == widthSpinner) || (arg0.getSource() == heightSpinner)) {
			BufferedImage resizedImage = new BufferedImage(((Integer)widthSpinner.getValue()).intValue(),((Integer)heightSpinner.getValue()).intValue(),BufferedImage.TYPE_INT_RGB);
			fractalImagePanel.setImage(resizedImage);
		}
		else if (arg0.getSource() == numFunctionsSpinner) {
			int numFunctions = ((Integer)numFunctionsSpinner.getValue()).intValue();
			IteratedFunction[] newFunctionList = new IteratedFunction[numFunctions];
			double[] newFunctionProbabilities = new double[numFunctions];
			System.arraycopy(currentFunctions, 0, newFunctionList, 0, Math.min(numFunctions,currentFunctions.length));
			((CardLayout)functionCards.getLayout()).show(functionCards, functionsComboBox.getItemAt(0));
			for (int functionNumber=currentFunctions.length-1;functionNumber>=numFunctions;functionNumber--) {
				String functionString = functionsComboBox.getItemAt(functionNumber);
				Component[] components = functionCards.getComponents();
				for(int componentNumber = components.length-1 ; componentNumber >=0 ; componentNumber--) {
					String componentName = components[componentNumber].getName();
				    if((componentName != null) && componentName.equals(functionString)) {
				        functionCards.getLayout().removeLayoutComponent(components[componentNumber]);
				    }
				}
				functionsComboBox.removeItemAt(functionNumber);
			}
			//Generate the list of max weights.
			double[] variationWeightLimits = new double[IteratedFunction.NUMBER_OF_VARIATIONS];
			for (int variationNumber=0;variationNumber<IteratedFunction.NUMBER_OF_VARIATIONS;variationNumber++) {
				variationWeightLimits[variationNumber] = ((Double)variationSpinners[variationNumber].getValue()).doubleValue();
			}
			for (int functionNumber=currentFunctions.length;functionNumber<numFunctions;functionNumber++) {
				String functionString = "Function "+functionNumber;
				functionsComboBox.addItem(functionString);
				JPanel ithFunctionPanel = new JPanel();
				functionCards.add(ithFunctionPanel, functionString);
				newFunctionList[functionNumber] = new IteratedFunction(variationWeightLimits);
				FunctionColorPanel functionColor = new FunctionColorPanel(newFunctionList[functionNumber],this);
				ithFunctionPanel.add(functionColor);

				functionColor.addChangeListener(this);
			}
			currentFunctions = newFunctionList;
			functionProbabilities = newFunctionProbabilities;
			double totalFunctionProbabilities = 0;
			for (int i=0;i<currentFunctions.length;i++) {
				functionProbabilities[i] = Math.random();
				totalFunctionProbabilities += functionProbabilities[i];
			}
			for (int i=0;i<currentFunctions.length;i++) {
				functionProbabilities[i] = functionProbabilities[i]/totalFunctionProbabilities;
			}	
		}
		updateImage();
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		Integer currentX = (Integer)xSpinner.getValue();
		Integer currentY = (Integer)ySpinner.getValue();
		if ((arg0.getX() != lastMouseX) || (arg0.getY() != lastMouseY)) {
			int newX = currentX.intValue()+(arg0.getX()-lastMouseX);
			int newY = currentY.intValue()+(arg0.getY()-lastMouseY);
			lastMouseX = arg0.getX();
			lastMouseY = arg0.getY();
			xSpinner.removeChangeListener(this);
			xSpinner.setValue(new Integer(newX));
			ySpinner.setValue(new Integer(newY));
			xSpinner.addChangeListener(this);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		lastMouseX = arg0.getX();
		lastMouseY = arg0.getY();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if ((arg0.getSource() == centerButton || arg0.getSource() == zeroButton)) {
			xSpinner.removeChangeListener(this);
			if (arg0.getSource() == centerButton) {
				xSpinner.setValue(new Integer(fractalImagePanel.getImage().getWidth()/2));
				ySpinner.setValue(new Integer(fractalImagePanel.getImage().getHeight()/2));
			}
			else if (arg0.getSource() == zeroButton) {
				xSpinner.setValue(new Integer(0));
				ySpinner.setValue(new Integer(0));
			}
			xSpinner.addChangeListener(this);
		}
		else if (arg0.getSource() == functionsComboBox) {
			((CardLayout)functionCards.getLayout()).show(functionCards, (String)functionsComboBox.getSelectedItem());
		}
		else if (arg0.getSource()==randomizeProbabilitiesButton) {
			double totalFunctionProbabilities = 0;
			for (int i=0;i<currentFunctions.length;i++) {
				functionProbabilities[i] = Math.random();
				totalFunctionProbabilities += functionProbabilities[i];
			}
			for (int i=0;i<currentFunctions.length;i++) {
				functionProbabilities[i] = functionProbabilities[i]/totalFunctionProbabilities;
			}
			updateImage();
		}
		else if (arg0.getSource() == newFractalButton) {
			newFractal();
		}
		else if (arg0.getSource() == saveItem) {
			//This lets the user save a file.
			JFileChooser jfc = new JFileChooser(); //Make the dialog.
			int result = jfc.showSaveDialog(this); //Bring it up; record the result.
			if(result == JFileChooser.CANCEL_OPTION)
				return;
			File f = jfc.getSelectedFile(); //This is the file that the user wants to overwrite or create.
			try {
			    BufferedImage bi = fractalImagePanel.getImage(); //Get the image from the fractal panel.
			    ImageIO.write(bi, "png", f); //Write it.
			} catch (IOException error) {
				//Something bad happened.
			    JOptionPane.showMessageDialog(this, "Error in saving file!", "Oh child!", JOptionPane.WARNING_MESSAGE);
			}
		}
		else if (arg0.getSource() == browseButton) {
			JFileChooser directoryBrowser = new JFileChooser();
			directoryBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			directoryBrowser.showSaveDialog(this);
			if (directoryBrowser.getSelectedFile().exists()) {
				outputField.setText(directoryBrowser.getSelectedFile().getAbsolutePath());
			}
			else {
				JOptionPane.showMessageDialog(this, "Directory "+directoryBrowser.getSelectedFile().getAbsolutePath()+" does not exist!", "Oh child!", JOptionPane.WARNING_MESSAGE);
			}
		}
		else if (arg0.getSource() == animateButton) {
			File od = new File(outputField.getText());
			if (od.exists() && od.isDirectory()) {
				outputDirectory = od;
				disableEverything(optionsPanel);
				fractalImagePanel.removeMouseListener(this);
				fractalImagePanel.removeMouseWheelListener(this);
				fractalImagePanel.removeMouseMotionListener(this);
				frames = ((Integer)framesSpinner.getValue()).intValue();
				cancelButton.setEnabled(true);
				fractalNumber = 0;
				animate();
			}
			else {
				JOptionPane.showMessageDialog(this, "Directory "+outputField.getText()+" does not exist!", "Oh child!", JOptionPane.WARNING_MESSAGE);
			}
		}
		else if (arg0.getSource() == cancelButton) {
			cancelled = true;
		}
	}
	
	public static void main(String[] args) {
		new FlameDisplay();
	}









}
