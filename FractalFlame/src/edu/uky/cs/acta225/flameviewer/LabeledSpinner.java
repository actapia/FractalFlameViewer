package edu.uky.cs.acta225.flameviewer;

import javax.swing.JLabel;
import javax.swing.JSpinner;

public class LabeledSpinner {
	private JSpinner spinner;
	private JLabel label;
	
	public LabeledSpinner(JLabel lab, JSpinner spin) {
		spinner = spin;
		label = lab;
	}

	public JSpinner getSpinner() {
		return spinner;
	}

	public JLabel getLabel() {
		return label;
	}
}
