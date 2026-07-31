package edu.uky.cs.acta225.flameviewer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uky.cs.acta225.flame.variation.NamedVariation;
import edu.uky.cs.acta225.flame.variation.ParameterizedVariation;

public class ParameterizedVariationControlPanel extends VariationControlPanel {
	private ParameterizedVariation variation;

	
	private class ParameterChanger implements ChangeListener {
		int index;
		
		
		public ParameterChanger(int i) {
			index = i;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			JSpinner spinner = (JSpinner)e.getSource();
			variation.setParameter(index, ((SpinnerNumberModel)spinner.getModel()).getNumber());
			sendChangeEvent(e);
		}
	}
	
	public ParameterizedVariationControlPanel(ParameterizedVariation vari) {
		super();
		variation = vari;
		
//		GridBagLayout layout = new GridBagLayout();
//		this.setLayout(layout);
//		GridBagConstraints labelConstraints = new GridBagConstraints();
//		
//		VariationFunctionControls.addGridBagSpinners(this, variation.getParameterNames(), );
		ArrayList<SpinnerNumberModel> models = new ArrayList<SpinnerNumberModel>();
		for (int i = 0; i < variation.getNumParameters(); i++) {
			Number param = variation.getParameter(i);
			if (param instanceof Double) {
				models.add(new SpinnerNumberModel(param.doubleValue(), variation.getMinimum(i).doubleValue(), variation.getMaximum(i).doubleValue(), 0.1));
			}
			else {
				models.add(new SpinnerNumberModel(param.intValue(), variation.getMinimum(i).intValue(), variation.getMaximum(i).intValue(), 1));
			}
			
		}
		
		ArrayList<LabeledSpinner> spinners = VariationFunctionControls.addGridBagSpinners(this, Arrays.asList(variation.getParameterNames()), models);
		for (int i = 0; i < spinners.size(); i++) {
			spinners.get(i).getSpinner().addChangeListener(new ParameterChanger(i));
		}
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Parameters"),BorderFactory.createEmptyBorder(10,10,10,10)));
		
	}

	@Override
	public NamedVariation getVariation() {
		return variation;
	}
	

}
