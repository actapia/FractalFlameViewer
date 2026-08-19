package edu.uky.cs.acta225.flameviewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import edu.uky.cs.acta225.flame.Named;

public class DetailsDisplayer implements ActionListener {
	private JFrame detailsFrame;
	
	public DetailsDisplayer(JFrame frame) {
		detailsFrame = frame;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		detailsFrame.setVisible(true);
		detailsFrame.requestFocus();
	}
}
