package edu.uky.cs.acta225.flameviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uky.cs.acta225.flame.IteratedFunction;
import edu.uky.cs.acta225.flame.variation.NamedVariation;
import edu.uky.cs.acta225.flameviewer.events.FunctionUpdateEvent;
import edu.uky.cs.acta225.flameviewer.events.FunctionUpdateListener;

public class FunctionDetailsPanel extends JPanel implements ChangeListener, FunctionUpdateListener {
	private IteratedFunction function;
	private ArrayList<LabeledSpinner> constantControls;
	private HashMap<NamedVariation, LabeledSpinner> variationWeightControls;
	private ArrayList<FunctionUpdateListener> functionUpdateListeners;
	private FunctionPanel functionPanel;
	private JPanel variationWeightsPanel;
	private GridBagLayout variatonWeightGridBagLayout;
	
	public void deleteVariation(NamedVariation variation) {
		function.removeVariation(variation);
		LabeledSpinner labeledSpinner = variationWeightControls.get(variation);
		variationWeightsPanel.remove(labeledSpinner.getLabel());
		variationWeightsPanel.remove(labeledSpinner.getSpinner());
		this.repaint();
		this.getParent().revalidate();
	}
	
	public void refreshVariationName(NamedVariation variation) {
		LabeledSpinner labeledSpinner = variationWeightControls.get(variation);
		labeledSpinner.getLabel().setText(variation.getName() + ":");
		labeledSpinner.getLabel().repaint();
		labeledSpinner.getLabel().getParent().revalidate();
	}
	
	public void addVariation(NamedVariation variation, Double weight) {
		function.addVariation(variation, weight);
		ArrayList<String> labels = new ArrayList<String>();
		labels.add(variation.getName() + ":");
		LabeledSpinner newSpinner = VariationFunctionControls.addGridBagSpinners(variationWeightsPanel, variatonWeightGridBagLayout, labels, VARIATION_MODEL).get(0);
		var spinner = newSpinner.getSpinner();
		spinner.setValue(weight);
		spinner.addChangeListener(new VariationWeightChanger(variation));
		variationWeightControls.put(variation, newSpinner);
		this.revalidate();
	}

	
	private class CoefficientChanger implements ChangeListener {
		int index;
		
		public CoefficientChanger(int ix) {
			index = ix;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			function.setParameter(index, ((Double)((JSpinner)e.getSource()).getValue()).doubleValue());
			functionUpdateFromChild(e);
		}
	}
	
	private class VariationWeightChanger implements ChangeListener {
		NamedVariation variation;
		
		public VariationWeightChanger(NamedVariation vr) {
			variation = vr;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			function.setVariationWeight(variation, ((Double)((JSpinner)e.getSource()).getValue()).doubleValue());
			functionUpdateFromChild(e);
		}
		
		
	}
	
	final static double CONSTANT_STEP = 0.1;
	final static SpinnerNumberModel VARIATION_MODEL = new SpinnerNumberModel(0, 0, Double.MAX_VALUE, CONSTANT_STEP);
	
	public FunctionDetailsPanel(JFrame parent, IteratedFunction fun, double weight, FunctionPanel fp) {
		super();
		
		function = fun;
		JPanel constantsPanel = new JPanel();
		ArrayList<String> constantLabels = new ArrayList<String>();
		for (int i = 0; i < function.numParameters(); i++) {
			constantLabels.add((char)('a' + i) + ":");
		}
		constantControls = VariationFunctionControls.addGridBagSpinners(constantsPanel, constantLabels, new SpinnerNumberModel(0.0, -Double.MAX_VALUE, Double.MAX_VALUE, CONSTANT_STEP));
		for (int i = 0; i < function.numParameters(); i++) {
			constantControls.get(i).getSpinner().setValue(function.getParameter(i));
			constantControls.get(i).getSpinner().addChangeListener(new CoefficientChanger(i));
		}
		variationWeightsPanel = new JPanel();
		JScrollPane variationWeightsScrollPane = new JScrollPane(variationWeightsPanel);
		ArrayList<String> variationLabels = new ArrayList<String>();
		for (NamedVariation variation: function.getVariations()) {
			variationLabels.add(variation.getName() + ":");
		}
		variatonWeightGridBagLayout = new GridBagLayout();
		variationWeightsPanel.setLayout(variatonWeightGridBagLayout);
		ArrayList<LabeledSpinner> variationWeightSpinners = VariationFunctionControls.addGridBagSpinners(variationWeightsPanel, variatonWeightGridBagLayout, variationLabels, VARIATION_MODEL);
		var entryIterator = function.getVariationWeights().entrySet().iterator();
		var spinnerIterator = variationWeightSpinners.iterator();
		variationWeightControls = new HashMap<NamedVariation, LabeledSpinner>();
		while (entryIterator.hasNext()) {
			LabeledSpinner labeledSpinner = spinnerIterator.next();
			JSpinner spinner = labeledSpinner.getSpinner();
			var entry = entryIterator.next();
			spinner.setValue(entry.getValue());
			spinner.addChangeListener(new VariationWeightChanger(entry.getKey()));
			variationWeightControls.put(entry.getKey(), labeledSpinner);
		}
		if (fp == null)
			functionPanel = new FunctionPanel(parent, function, weight, null, null, false);
		else
			functionPanel = fp.createLinked(false);
		this.add(functionPanel);
		variationWeightsScrollPane.setPreferredSize(new Dimension(200, 170));
		this.add(constantsPanel);
		constantsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Coefficients"),BorderFactory.createEmptyBorder(10,10,10,10)));
		
		variationWeightsScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Variation weights"),BorderFactory.createEmptyBorder(10,10,10,10)));
		this.add(variationWeightsScrollPane);
		functionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("General settings"),BorderFactory.createEmptyBorder(10,10,10,10)));
		functionUpdateListeners = new ArrayList<FunctionUpdateListener>();
		functionPanel.addFunctionUpdateListener(this);
	}

	
	private void functionUpdate(FunctionUpdateEvent event) {
		for (FunctionUpdateListener listener: functionUpdateListeners) {
			listener.functionChanged(event);
		}
	}
	
	private void functionUpdateFromChild(EventObject child) {
		functionUpdate(new FunctionUpdateEvent(this, child));
	}

	@Override
	public void functionChanged(FunctionUpdateEvent event) {
		functionUpdateFromChild(event);
	}
	
	public void addFunctionUpdateListener(FunctionUpdateListener listener) {
		functionUpdateListeners.add(listener);
	}
	
	public FunctionPanel getFunctionPanel() {
		return functionPanel;
	}
	
	public void updateControls() {
		functionPanel.updateControls();
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}

	public IteratedFunction getFunction() {
		return function;
	}
}
