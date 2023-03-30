import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import Other.ImageUtilities;

public class FunctionColorPanel extends ColorPanel {
	IteratedFunction function;
	public FunctionColorPanel(IteratedFunction func, JFrame parentFrame) {
		super(parentFrame);
		function = func;
		setColor(new Color(ImageUtilities.getRedComponent(function.getColor()),ImageUtilities.getGreenComponent(function.getColor()),ImageUtilities.getBlueComponent(function.getColor())));
	}
	
	@Override
	public void paint(Graphics g) {
		this.setBackground(new Color(ImageUtilities.getRedComponent(function.getColor()),ImageUtilities.getGreenComponent(function.getColor()),ImageUtilities.getBlueComponent(function.getColor())));
		super.paint(g);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		function.setColor(ImageUtilities.combine(ImageUtilities.MAX_COLOR,super.getColor().getRed(),super.getColor().getGreen(),super.getColor().getBlue()));
		this.repaint();
	}
}
