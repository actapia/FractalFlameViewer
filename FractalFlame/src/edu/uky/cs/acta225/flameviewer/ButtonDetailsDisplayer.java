package edu.uky.cs.acta225.flameviewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import edu.uky.cs.acta225.flame.Named;

public class ButtonDetailsDisplayer implements ActionListener {
	private Named object;
	private JComboBox<Object> detailsComboBox;
	private JFrame detailsFrame;
	
	public ButtonDetailsDisplayer(Named o, JComboBox<Object> box, JFrame frame) {
		object = o;
		detailsComboBox = box;
		detailsFrame = frame;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		detailsComboBox.setSelectedItem(object.getName());
		detailsFrame.setVisible(true);
		detailsFrame.requestFocus();
	}
}
