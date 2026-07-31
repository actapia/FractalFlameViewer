package edu.uky.cs.acta225.flameviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import edu.uky.cs.acta225.flame.IteratedFunction;
import edu.uky.cs.acta225.imageutils.ImageUtilities;

public class FunctionPanel extends JPanel implements ChangeListener, FocusListener, KeyListener, ActionListener {
	private IteratedFunction function;
	private ArrayList<FunctionUpdateListener> listeners;
	private JSpinner probabilitySpinner;
	private ColorPanel colorSelectionPanel;
	private JFrame parentFrame;
	private JTextField nameField;
	private JButton detailsButton;
	private JButton deleteButton;
	
	public FunctionPanel(JFrame parentFrm, IteratedFunction f, Double probability, SpinnerNumberModel probabilitySpinnerModel, Document nameDocument, boolean addDetailsButton) {
		super();
		final double PROBABILITY_STEP = 1;
		final int NUM_COLUMNS = 2;
		final int NUM_ROWS = 1;
		final int SPINNER_HEIGHT = 20;
		final int SPINNER_WIDTH = 150;
		Dimension spinnerDimensions = new Dimension(SPINNER_WIDTH,SPINNER_HEIGHT);
		function = f;
//		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//		JPanel probabilityPanel = new JPanel();
//		JPanel colorPanel = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);
		JLabel probabilityLabel = new JLabel("Frequency:");
		JLabel colorLabel = new JLabel("Color:");
		JLabel nameLabel = new JLabel("Name:");
		if (probabilitySpinnerModel == null)
			probabilitySpinnerModel = new SpinnerNumberModel(probability.doubleValue(), 0, Integer.MAX_VALUE, PROBABILITY_STEP);
		probabilitySpinner = new JSpinner(probabilitySpinnerModel);
		probabilitySpinner.setPreferredSize(spinnerDimensions);
		parentFrame = parentFrm;
		colorSelectionPanel = new ColorPanel(parentFrame);;
		Color ac = ImageUtilities.toAwtColor(f.getColor());
		colorSelectionPanel.setPreferredSize(spinnerDimensions);
		System.out.println(ac);
		colorSelectionPanel.setColor(ac);
		nameField = new JTextField();
		if (nameDocument != null) {
			nameField.setDocument(nameDocument);
		}
		GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.weightx = 1.0;
		labelConstraints.gridwidth = 1;
		labelConstraints.ipadx = 10;
		labelConstraints.anchor = GridBagConstraints.WEST;
		gbl.setConstraints(nameLabel, labelConstraints);
		this.add(nameLabel);
		GridBagConstraints controlConstraints = new GridBagConstraints();
		controlConstraints.weightx = 3.0;
		controlConstraints.ipadx = 0;
		controlConstraints.anchor = GridBagConstraints.CENTER;
		controlConstraints.gridwidth = GridBagConstraints.REMAINDER;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(nameField, controlConstraints);
		this.add(nameField);
		gbl.setConstraints(probabilityLabel, labelConstraints);
		this.add(probabilityLabel);
		gbl.setConstraints(probabilitySpinner, controlConstraints);
		this.add(probabilitySpinner);
		gbl.setConstraints(colorLabel, labelConstraints);
		this.add(colorLabel);
		gbl.setConstraints(colorSelectionPanel, controlConstraints);
		this.add(colorSelectionPanel);
		detailsButton = new JButton("Details");
		deleteButton = new JButton("Delete");
		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
		buttonConstraints.weightx = 1.0;
		buttonConstraints.gridwidth = GridBagConstraints.REMAINDER;
		JPanel buttonPanel = new JPanel();
		if (addDetailsButton) {
			buttonConstraints.gridwidth = 1;
			gbl.setConstraints(detailsButton, buttonConstraints);
			buttonPanel.add(detailsButton);
		}
		buttonConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gbl.setConstraints(buttonPanel, buttonConstraints);
		buttonPanel.add(deleteButton);
		this.add(buttonPanel);
		colorSelectionPanel.addChangeListener(this);
		probabilitySpinner.addChangeListener(this);
		listeners = new ArrayList<FunctionUpdateListener>();
//		if (nameDocument == null)
//			nameField.getDocument().addDocumentListener(this);
		nameField.setText(function.getName());
		nameField.addFocusListener(this);
		nameField.addKeyListener(this);
		deleteButton.addActionListener(this);
	}
	
	public FunctionPanel createLinked(boolean addDetailsButton) {
		return new FunctionPanel(parentFrame, function, null, (SpinnerNumberModel)this.probabilitySpinner.getModel(), nameField.getDocument(), addDetailsButton);
	}
	
	public JButton getDetailsButton() {
		return detailsButton;
	}
	
	public IteratedFunction getFunction() {
		return function;
	}
	
	public JSpinner getProbabilitySpinner() {
		return probabilitySpinner;
	}
	
	public void addFunctionUpdateListener(FunctionUpdateListener listener) {
		listeners.add(listener);
	}
	
	public void updateControls() {
		colorSelectionPanel.setColor(ImageUtilities.toAwtColor(function.getColor()));
	}
	
	private void functionUpdate(FunctionUpdateEvent event) {
		for (FunctionUpdateListener listener: listeners) {
			listener.functionChanged(event);
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == colorSelectionPanel) {
			function.setColor(ImageUtilities.fromAwtColor(colorSelectionPanel.getColor()));
		}
		functionUpdate(new FunctionUpdateEvent(this, e));
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public JTextField getNameField() {
		return nameField;
	}
	
	public void nameChanged(EventObject e) {
		functionUpdate(new FunctionUpdateEvent(this, e));
	}

	@Override
	public void focusLost(FocusEvent e) {
		nameChanged(e);
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
			nameChanged(e);
		}
	}
	
	public JButton getDeleteButton() {
		return deleteButton;
	}
	
	private void delete(EventObject e) {
		functionUpdate(new FunctionUpdateEvent(this, e));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == deleteButton) {
			delete(e);
		}
	}

	
	
}
