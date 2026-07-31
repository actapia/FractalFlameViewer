package edu.uky.cs.acta225.flameviewer;

import javax.swing.JPanel;

import edu.uky.cs.acta225.flame.variation.BasicVariation;
import edu.uky.cs.acta225.flame.variation.NamedVariation;
import edu.uky.cs.acta225.flame.variation.ParameterizedVariation;
import edu.uky.cs.acta225.flame.variation.VariationVisitor;

public class VariationControlMaker implements VariationVisitor<JPanel> {
	private static VariationControlMaker singleton;
	
	private VariationControlMaker() {}
	
	private static VariationControlMaker getSingleton() {
		if (singleton == null) {
			singleton = new VariationControlMaker();
		}
		return singleton;
	}
	
	public static VariationControlPanel createController(NamedVariation vari) {
		return (VariationControlPanel)vari.accept(getSingleton());
	}

	@Override
	public JPanel visit(BasicVariation vari) {
		return null;
	}

	@Override
	public JPanel visit(ParameterizedVariation vari) {
		return new ParameterizedVariationControlPanel(vari);
	}

}
