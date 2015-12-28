package eyedev._17;

import prophecy.common.image.BWImage;

import java.awt.*;

public class BaseLineFinder {
  public MarkLine findBaseLine(BWImage image) {
    int w = image.getWidth(), h = image.getHeight();
    for (int y = h-1; y >= 0; y--) {
      double whiteness = lineWhiteness(image, w, y);
      if (whiteness < 0.9) {
        //System.out.println("Base line found: " + y);
        return new MarkLine(MarkLine.Type.base, 0, y+1, w);
      }
    }
    return null;
  }

  private double lineWhiteness(BWImage image, int w, int y) {
    return image.clip(new Rectangle(0, y, w, 1)).averageBrightness();
  }
}
