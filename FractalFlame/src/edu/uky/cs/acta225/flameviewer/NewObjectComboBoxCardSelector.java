package edu.uky.cs.acta225.flameviewer;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public abstract class NewObjectComboBoxCardSelector extends ComboBoxCardSelector {
	private ComboBoxItem newItem; 

	public NewObjectComboBoxCardSelector(JComboBox<Object> box, JPanel cards, ComboBoxItem nItem) {
		super(box, cards);
		newItem = nItem;
	}
	
	protected abstract Object newObject();
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(newItem);
		if (comboBox.getSelectedItem() == newItem) {
			System.out.println("newitem");
			comboBox.setSelectedItem(newObject());
		}
		System.out.println("NOCBCS");
		super.actionPerformed(e);
	}
}
