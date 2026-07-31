package edu.uky.cs.acta225.flameviewer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uky.cs.acta225.flame.variation.NamedVariation;

public class VariationsPanel extends VariationDeletingPanel {
	private LinkedHashMap<NamedVariation, Double> variationWeights;
	private HashMap<NamedVariation, VariationComponents> variationComponents;
	private JPanel weightsPanel;

	private class WeightChanger implements ChangeListener {
		private NamedVariation variation;
		
		public WeightChanger(NamedVariation vari) {
			variation = vari;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			JSpinner spinner = (JSpinner)e.getSource();
			variationWeights.put(variation, (Double)spinner.getValue());
		}
	}
	
	private class FunctionDeleter implements ActionListener {
		private NamedVariation variation;
		
		public FunctionDeleter(NamedVariation vari) {
			variation = vari;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			deleteVariation(variation);
			sendDeletedEvent(e, variation);
		}
	}
	
	public void deleteVariation(NamedVariation vari) {
		variationWeights.remove(vari);
		VariationComponents components = variationComponents.get(vari);
		weightsPanel.remove(components.getLabel());
		weightsPanel.remove(components.getSpinner());
		weightsPanel.remove(components.getDetailsButton());
		weightsPanel.remove(components.getDeleteButton());
	}
	
	public void addDetailsListener(NamedVariation vari, ActionListener listener) {
		variationComponents.get(vari).getDetailsButton().addActionListener(listener);
	}
	
	public VariationsPanel(LinkedHashMap<NamedVariation, Double> varWeights) {
		super();
		final double WEIGHT_STEP = 0.1;
		final int SPINNER_HEIGHT = 20;
		final int SPINNER_WIDTH = 150;
		variationWeights = varWeights;
		weightsPanel = new JPanel();
		JScrollPane weightsScrollPane = new JScrollPane(weightsPanel);
		weightsScrollPane.setPreferredSize(new Dimension(400, 170));
//		weightsScrollPane.setBorder();
		GridBagLayout gbl = new GridBagLayout();
		weightsPanel.setLayout(gbl);
		GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.weightx = 1.0;
		labelConstraints.gridwidth = 1;
		labelConstraints.ipadx = 10;
		labelConstraints.anchor = GridBagConstraints.WEST;
		GridBagConstraints middleConstraints = new GridBagConstraints();
		middleConstraints.weightx = 1.0;
		middleConstraints.gridwidth = 1;
		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.weightx = 1.0;
		buttonConstraints.gridwidth = 1;
		buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
		buttonConstraints.insets = new Insets(0, 0, 0, 5);
		GridBagConstraints endConstraints = new GridBagConstraints();
		endConstraints.weightx = 1.0;
		endConstraints.fill = GridBagConstraints.HORIZONTAL;
		endConstraints.gridwidth = GridBagConstraints.REMAINDER;
		endConstraints.insets = new Insets(0, 0, 0, 5);
		variationComponents = new HashMap<NamedVariation, VariationComponents>();
		for (var entry: variationWeights.entrySet()) {
//			ArrayList<JComponent> components = new ArrayList<JComponent>();
			JLabel variationLabel =  new JLabel(entry.getKey().getName() + ":");
			gbl.setConstraints(variationLabel, labelConstraints);
			weightsPanel.add(variationLabel);
			SpinnerNumberModel model = new SpinnerNumberModel(entry.getValue().doubleValue(), 0, Double.MAX_VALUE, WEIGHT_STEP);
			JSpinner spinner = new JSpinner(model);
			spinner.setPreferredSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
			gbl.setConstraints(spinner, middleConstraints);
			weightsPanel.add(spinner);
			JButton detailsButton = new JButton("Details");
			gbl.setConstraints(detailsButton, buttonConstraints);
			weightsPanel.add(detailsButton);
			JButton deleteButton = new JButton("Delete");
			gbl.setConstraints(deleteButton, endConstraints);
			weightsPanel.add(deleteButton);
			variationComponents.put(entry.getKey(), new VariationComponents(variationLabel, spinner, detailsButton, deleteButton));
			spinner.addChangeListener(new WeightChanger(entry.getKey()));
			deleteButton.addActionListener(new FunctionDeleter(entry.getKey()));
		}
		this.add(weightsScrollPane);
		
	}

	public void renameVariation(NamedVariation variation, String newName) {
		JLabel label = variationComponents.get(variation).getLabel();
		label.setText(newName);
		label.repaint();
		label.getParent().revalidate();
	}
}
