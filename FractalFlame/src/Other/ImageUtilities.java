package Other;


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;


public class ImageUtilities {
	
	public final static int MAX_COLOR = 255;
	
	public static int getAlphaComponent(int color) { 
		 return (color & (0xFF000000))>>24;
	}
	public static int getRedComponent(int color) {
		return (color & (0x00FF0000))>>16;
	}	
	public static int getGreenComponent(int color) {
		return (color & (0x0000FF00))>>8;
	}
	public static int getBlueComponent(int color) {
		return (color & (0x000000FF)); 
	}
	public static int combine(int a, int r, int g, int b){ //This function originally written by Jason Reed. Contact: jason.reed@fayette.kyschools.us
		//Make sure that nothing is too big or too small.
		r = Math.max(Math.min(255,r), 0);
		g = Math.max(Math.min(255,g), 0);
		b = Math.max(Math.min(255,b), 0);
		a = a<<24; //Shift alpha back to its proper position.
		r=r<<16; //Shift red back to its proper position.
		g=g<<8; //Shift green back to its proper position.
		return a|r|g|b; //Put them all together.
	}
	
	public static void toGrayscale(BufferedImage img) {
		for (int r=0;r<img.getHeight();r++) {
			for (int c=0;c<img.getWidth();c++) {
				int val = img.getRGB(c,r);
				int avg = (int)Math.round((getBlueComponent(val)+getRedComponent(val)+getGreenComponent(val))/3.0);
				//System.out.println(avg);
				img.setRGB(c, r, combine(255,avg,avg,avg));
			}
		}
	}
	public static Image cloneImage(BufferedImage im) {
		ColorModel cm = im.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = im.copyData(null);
		Image img = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		return img;
	}
	public static BufferedImage convertModel(BufferedImage src, int bufImgType) {
	    BufferedImage img= new BufferedImage(src.getWidth(), src.getHeight(), bufImgType);
	    Graphics2D g2d= img.createGraphics();
	    g2d.drawImage(src, 0, 0, null);
	    g2d.dispose();
	    return img;
	}
}
