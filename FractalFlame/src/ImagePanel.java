import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class ImagePanel extends JPanel{
	private BufferedImage img; //This is the image that the ImagePanel contains.
	public ImagePanel() {
		super();
	}
	public ImagePanel(BufferedImage i) {
		this();
		img = i;
		setPreferredSize(new Dimension(i.getWidth(),i.getHeight()));
	}
	
	public void setImage(BufferedImage i) {
		img=i;
		setPreferredSize(new Dimension(i.getWidth(),i.getHeight()));
	} //Mutator for the image.
	public BufferedImage getImage() {return img;} //Accessor for the image.
	public void paint(Graphics g) {
			//The following 2 lines adapted from code given to me by Jason Reed for a project in Computer Science 2. Contact: jason.reed@fayette.kyschools.us
			super.paint(g); //Paint the normal JPanel stuff.
			((Graphics2D)g).drawImage(img,null,0,0); //Draw the image.
	}
	public int[][] imgToArray(){ //This changes the image into an array that we can work with.
		int[] pix = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
		int[][] pixels = new int[img.getHeight()][img.getWidth()]; //The size of the 2d array should be the same as the dimensions of the image.
		
		for (int r=0;r<img.getHeight();r++) {
			for (int c=0;c<img.getWidth();c++) {
				pixels[r][c] = pix[r*img.getWidth()+c]; //Go c past r widths in the pix array to find the correct value.
			}
		}
		return pixels;
	}
	public void imageFromArray(int[][] pixels,int type) { //This takes an array representation of an image and makes it into the actual image.
			//Set the lengths/widths.
			int w = pixels[0].length;
			int h = pixels.length;

			this.setPreferredSize(new Dimension(w,h));
			img = new BufferedImage(w,h,type);
			//Transfer the values from the array to the image.
			for (int r=0;r<h;r++) {
				for (int c=0;c<w;c++) {
					img.setRGB(c,r,pixels[r][c]);
				}
			}
			this.repaint();
			this.revalidate();
			//This fixes the scrollbar glitch.
			this.getParent().repaint();
			this.getParent().validate();
	}

	
}
