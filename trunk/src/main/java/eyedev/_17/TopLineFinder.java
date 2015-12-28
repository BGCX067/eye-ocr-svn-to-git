package eyedev._17;

import prophecy.common.image.BWImage;

import java.awt.*;

public class TopLineFinder {
  public MarkLine findTopLine(BWImage image) {
    MarkLine markLine = findLineBelowAccents(image);
    if (markLine != null) return markLine;
    return new MarkLine(MarkLine.Type.top, 0, -1, image.getWidth());
  }

  private MarkLine findLineBelowAccents(BWImage image) {
    int w = image.getWidth(), h = image.getHeight();
    int maxY = (int) (h*0.3);
    int y = 0;
    while (y < maxY && lineWhiteness(image, w, y) == 1.0) ++y; // skip white stuff on top (should only be 1 pixel)
    for (; y < maxY; y++) {
      double whiteness = lineWhiteness(image, w, y);
      //System.out.println("y=" + y + ", whiteness=" + whiteness + ", maxY=" + maxY);
      if (whiteness == 1.0) {
        while (y+1 < maxY && lineWhiteness(image, w, y+1) == 1.0) ++y;
        //System.out.println("top: " + y);
        //System.out.println("Base line found: " + y);
        return new MarkLine(MarkLine.Type.top, 0, y, w);
      }
    }
    return null;
  }

  private double lineWhiteness(BWImage image, int w, int y) {
    return image.clip(new Rectangle(0, y, w, 1)).averageBrightness();
  }
}
