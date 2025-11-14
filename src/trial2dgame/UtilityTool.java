package trial2dgame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class UtilityTool {
	
	public BufferedImage scaleImage(BufferedImage original, int width, int height) {
		
		BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
		Graphics2D g2 = scaledImage.createGraphics(); //creates Grphic2D, used to draw into this BfrImage
		g2.drawImage(original, 0, 0, width, height, null); //draw tile[]img into the scaledImage (BfrImage) that this Graphics2D is linked to
		g2.dispose();
		
		return scaledImage;
	}
}
