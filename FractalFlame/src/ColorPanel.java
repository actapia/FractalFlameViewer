import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Other.ImageUtilities;

public class ColorPanel extends JPanel implements MouseListener {
	private JDialog colorChooserDialog;
	private JColorChooser colorChooser;
	private ArrayList<ChangeListener> changeListeners;

	public ColorPanel(JFrame parentFrame) {
		super();
		final int COLOR_DIALOG_WIDTH = 500;
		final int COLOR_DIALOG_HEIGHT = 500;
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.addMouseListener(this);
		colorChooserDialog = new JDialog(parentFrame);
		colorChooserDialog.setModalityType(ModalityType.DOCUMENT_MODAL);
		colorChooser = new JColorChooser();
		changeListeners = new ArrayList<ChangeListener>();
		colorChooserDialog.add(colorChooser);
		colorChooserDialog.setSize(COLOR_DIALOG_WIDTH, COLOR_DIALOG_HEIGHT);
	}
	
	public Color getColor() {
		return colorChooser.getColor();
	}
	
	public void setColor(Color c) {
		colorChooser.setColor(c);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	
	public void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (this.isEnabled()) {
			Color currentColor = getColor();
			colorChooserDialog.setVisible(true);
			repaint();
			if (!currentColor.equals(getColor())) {
				for (ChangeListener listener: changeListeners) {
					listener.stateChanged(new ChangeEvent(this));
				}
			}
		}
	}

}
