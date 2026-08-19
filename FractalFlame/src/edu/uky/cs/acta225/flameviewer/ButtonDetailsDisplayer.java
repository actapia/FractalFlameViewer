package edu.uky.cs.acta225.flameviewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import edu.uky.cs.acta225.flame.Named;

public class ButtonDetailsDisplayer extends DetailsDisplayer {
	private Named object;
	private JComboBox<Object> detailsComboBox;
	
	public ButtonDetailsDisplayer(Named o, JComboBox<Object> box, JFrame frame) {
		super(frame);
		object = o;
		detailsComboBox = box;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		detailsComboBox.setSelectedItem(object.getName());
		super.actionPerformed(e);
	}
}
