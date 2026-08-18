package edu.uky.cs.acta225.flameviewer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.uky.cs.acta225.flame.variation.EyefishVariation;
import edu.uky.cs.acta225.flame.variation.HorseshoeVariation;
import edu.uky.cs.acta225.flame.variation.LinearVariation;
import edu.uky.cs.acta225.flame.variation.NamedVariation;
import edu.uky.cs.acta225.flame.variation.PopcornVariation;
import edu.uky.cs.acta225.flame.variation.SinusoidalVariation;
import edu.uky.cs.acta225.flame.variation.SphericalVariation;
import edu.uky.cs.acta225.flame.variation.SwirlVariation;
import edu.uky.cs.acta225.flameviewer.events.VariationChangeEvent;
import edu.uky.cs.acta225.flameviewer.events.VariationChangeListener;
import edu.uky.cs.acta225.flameviewer.events.VariationNameChangeRequestListener;
import edu.uky.cs.acta225.flameviewer.events.VariationNameChangeRequestedEvent;
import edu.uky.cs.acta225.flameviewer.events.VariationReplacedEvent;
import edu.uky.cs.acta225.flameviewer.events.VariationReplacementListener;

public class VariationDetailsPanel extends VariationDeletingPanel implements ActionListener, FocusListener, KeyListener, VariationChangeListener {
	private NamedVariation variation;
	private JButton deleteButton;
	private ArrayList<VariationNameChangeRequestListener> nameChangeRequestListeners;
	private ArrayList<VariationChangeListener> variationChangeListeners;
	private ArrayList<VariationReplacementListener> variationReplacementListeners;
	private JTextField nameField;
	private VariationControlPanel specialPanel;
	private JComboBox<Object> typeComboBox;
	
	private static Map.Entry<String, Supplier<NamedVariation>> variationEntry(Supplier<NamedVariation> supp) {
		return Map.entry(supp.get().getVariationTypeName(), supp);
	}
	
	public static Map<String, Supplier<NamedVariation>> variationCreators = Map.ofEntries(
			variationEntry(EyefishVariation::new),
			variationEntry(HorseshoeVariation::new),
			variationEntry(LinearVariation::new),
			variationEntry(PopcornVariation::new),
			variationEntry(SinusoidalVariation::new),
			variationEntry(SphericalVariation::new),
			variationEntry(SwirlVariation::new)
	);
	
	public VariationDetailsPanel(NamedVariation vari) {
		super();
		final int GENERAL_PANEL_WIDTH = 170;
		final int GENERAL_PANEL_HEIGHT = 100;
		variation = vari;
		JPanel generalPanel = new JPanel();
		generalPanel.setPreferredSize(new Dimension(GENERAL_PANEL_WIDTH, GENERAL_PANEL_HEIGHT));
		GridBagLayout generalLayout = new GridBagLayout();
		generalPanel.setLayout(generalLayout);
		GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.weightx = 1.0;
		labelConstraints.gridwidth = 1;
		labelConstraints.ipadx = 10;
		labelConstraints.anchor = GridBagConstraints.WEST;
		JLabel typeLabel = new JLabel("Type:");
		generalLayout.setConstraints(typeLabel, labelConstraints);
		generalPanel.add(typeLabel);
//		GridBagConstraints rightLabelConstraints = new GridBagConstraints();
//		rightLabelConstraints.weightx = 1.0;
//		rightLabelConstraints.gridwidth = GridBagConstraints.REMAINDER;
////		rightLabelConstraints.ipadx = 10;
//		rightLabelConstraints.anchor = GridBagConstraints.WEST;
//		JLabel typeValueLabel = new JLabel(variation.getVariationTypeName());
//		generalLayout.setConstraints(typeValueLabel, rightLabelConstraints);
//		generalPanel.add(typeValueLabel);
		
		GridBagConstraints controlConstraints = new GridBagConstraints();
		controlConstraints.weightx = 1.0;
		controlConstraints.gridwidth = GridBagConstraints.REMAINDER;
//		controlConstraints.ipadx = 10;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		
		typeComboBox = new JComboBox<Object>();
		for (String typeName: variationCreators.keySet()) {
			typeComboBox.addItem(typeName);
		}
		typeComboBox.setSelectedItem(variation.getVariationTypeName());
		typeComboBox.addActionListener(this);
		generalLayout.setConstraints(typeComboBox, controlConstraints);
		generalPanel.add(typeComboBox);
		JLabel nameLabel = new JLabel("Name:");
		generalLayout.setConstraints(nameLabel, labelConstraints);
		generalPanel.add(nameLabel);

		nameField = new JTextField();
		nameField.setText(vari.getName());
		generalLayout.setConstraints(nameField, controlConstraints);
		generalPanel.add(nameField);
		JPanel buttonsPanel = new JPanel();
		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
		buttonConstraints.weightx = 1.0;
		buttonConstraints.gridwidth = GridBagConstraints.REMAINDER;
		deleteButton = new JButton("Delete");
		buttonsPanel.add(deleteButton);
		generalLayout.setConstraints(buttonsPanel, buttonConstraints);
		generalPanel.add(buttonsPanel);
		generalPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("General settings"),BorderFactory.createEmptyBorder(10,10,10,10)));
		this.add(generalPanel);
		makeSpecialPanel();
		deleteButton.addActionListener(this);
		nameField.addFocusListener(this);
		nameField.addKeyListener(this);
		nameChangeRequestListeners = new ArrayList<VariationNameChangeRequestListener>();
		variationChangeListeners = new ArrayList<VariationChangeListener>();
		variationReplacementListeners = new ArrayList<VariationReplacementListener>();
	}
	
	private void makeSpecialPanel() {
		specialPanel = VariationControlMaker.createController(variation);
		if (specialPanel != null) {
			this.add(specialPanel);
			specialPanel.addVariationChangeListener(this);
			this.revalidate();
		}
	}
	
	private void deleteSpecialPanel() {
		if (specialPanel != null) {
			this.remove(specialPanel);
			specialPanel = null;
			this.revalidate();
		}
	}
	
	public void addNameChangeRequestListener(VariationNameChangeRequestListener listener) {
		nameChangeRequestListeners.add(listener);
	}
	
	private void sendNameChangeRequest(EventObject parent) {
		VariationNameChangeRequestedEvent event = new VariationNameChangeRequestedEvent(this, parent, variation, nameField.getText());
		for (VariationNameChangeRequestListener listener: nameChangeRequestListeners) {
			listener.variationNameChangeRequested(event);
		}
	}
	
	public void addVariationChangelistener(VariationChangeListener listener) {
		variationChangeListeners.add(listener);
	}
	
	private void sendVariationChangeEvent(EventObject parent) {
		VariationChangeEvent event = new VariationChangeEvent(this, parent, variation);
		for (VariationChangeListener listener: variationChangeListeners) {
			listener.variationChanged(event);
		}
	}
	
	public void addVariationReplacementListener(VariationReplacementListener listener) {
		variationReplacementListeners.add(listener);
	}
	
	private void sendVariationReplacedEvent(EventObject parent, NamedVariation oldVariation, NamedVariation newVariation) {
		VariationReplacedEvent event = new VariationReplacedEvent(this, parent, oldVariation, newVariation);
		for (VariationReplacementListener listener: variationReplacementListeners) {
			listener.variationReplaced(event);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == deleteButton) {
			sendDeletedEvent(e, variation);
		}
		else if ((e.getSource() == typeComboBox) && !typeComboBox.getSelectedItem().equals(variation.getVariationTypeName())) {
			System.out.println("type change");
			NamedVariation newVariation = variationCreators.get(typeComboBox.getSelectedItem()).get();
			NamedVariation oldVariation = variation;
			variation = newVariation;
			deleteSpecialPanel();
			makeSpecialPanel();
			sendVariationReplacedEvent(e, oldVariation, newVariation);
			
			
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			sendNameChangeRequest(e);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		sendNameChangeRequest(e);
	}

	@Override
	public void variationChanged(VariationChangeEvent event) {
		sendVariationChangeEvent(event);
	}
}
