package edu.uky.cs.acta225.flameviewer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;

public class VariationComponents {
	private JLabel label;
	private JSpinner spinner;
	private JButton detailsButton;
	private JButton deleteButton;
	
	public VariationComponents(JLabel l, JSpinner s, JButton details, JButton delete) {
		label = l;
		spinner = s;
		detailsButton = details;
		deleteButton = delete;
	}

	public JLabel getLabel() {
		return label;
	}

	public JSpinner getSpinner() {
		return spinner;
	}

	public JButton getDetailsButton() {
		return detailsButton;
	}

	public JButton getDeleteButton() {
		return deleteButton;
	}
}
