package edu.uky.cs.acta225.flameviewer;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class ComboBoxCardSelector implements ActionListener {
	protected JComboBox<Object> comboBox;
	private JPanel cardsPanel;
	
	public ComboBoxCardSelector(JComboBox<Object> box, JPanel cards) {
		comboBox = box;
		cardsPanel = cards;
	}
	
	public void changeCard() {
		((CardLayout)cardsPanel.getLayout()).show(cardsPanel, (String)comboBox.getSelectedItem());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
//		if (comboBox.getSelectedItem() == newFunctionItem) {
////			System.out.println("New function!");
//			IteratedFunction fun = newFunction();
//			functionsComboBox.setSelectedItem(fun.getName());
//			updateImage();
//		}
//		System.out.println("Selected " + functionsComboBox.getSelectedItem());
		changeCard();
	}

}
