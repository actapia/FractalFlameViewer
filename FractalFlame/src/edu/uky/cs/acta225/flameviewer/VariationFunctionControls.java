package edu.uky.cs.acta225.flameviewer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class VariationFunctionControls {
	public static ArrayList<LabeledSpinner> addGridBagSpinners(JPanel panel, List<String> labels, List<SpinnerNumberModel> models) {
		final int SPINNER_HEIGHT = 20;
		final int SPINNER_WIDTH = 75;
		Dimension spinnerDimensions = new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT);
		GridBagLayout gbl = new GridBagLayout();
		panel.setLayout(gbl);
		GridBagConstraints con = new GridBagConstraints();
		ArrayList<LabeledSpinner> controls = new ArrayList<LabeledSpinner>();
		Iterator<String> labelIter = labels.iterator();
		Iterator<SpinnerNumberModel> modelIter = models.iterator();
		while (labelIter.hasNext()) {
			var labelText = labelIter.next();
			var model = modelIter.next();
			JLabel label = new JLabel(labelText);
			JSpinner spinner = new JSpinner(model);
//			if (i == spinnerLabels.length - 1) {
//				con.gridheight
//			}
			con.weightx = 1.0;
			con.gridwidth = 1;
			con.ipadx = 10;
			con.anchor = GridBagConstraints.WEST;
			gbl.setConstraints(label, con);
			panel.add(label);
			con.weightx = 3.0;
			con.ipadx = 0;
			con.anchor = GridBagConstraints.CENTER;
			con.gridwidth = GridBagConstraints.REMAINDER;
			gbl.setConstraints(spinner, con);
			spinner.setPreferredSize(spinnerDimensions);
			panel.add(spinner);
//			spinner.setValue(function.getParameter(i));
			controls.add(new LabeledSpinner(label, spinner));
		}
		return controls;
	}
	
	public static ArrayList<LabeledSpinner> addGridBagSpinners(JPanel panel, List<String> labels, SpinnerNumberModel model) {
		ArrayList<SpinnerNumberModel> models = new ArrayList<SpinnerNumberModel>();
		for (String label: labels) {
			models.add(new SpinnerNumberModel(model.getNumber(), model.getMinimum(), model.getMaximum(), model.getStepSize()));
		}
		return addGridBagSpinners(panel, labels, models);
	}
}
