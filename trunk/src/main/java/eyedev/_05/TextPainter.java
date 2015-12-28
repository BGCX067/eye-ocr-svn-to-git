package eyedev._05;

import prophecy.common.image.BWImage;
import prophecy.common.image.RGBImage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TextPainter {
  public static BWImage paintText(int w, int h, int x, int y, Font font, String text) {
    BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);

    Graphics g = image.getGraphics();
    g.setColor(Color.white);
    g.fillRect(0, 0, image.getWidth(), image.getHeight());
    g.setColor(Color.black);
    g.setFont(font);
    g.drawString(text, x, y);

    return new RGBImage(image).toBW();
  }
}
