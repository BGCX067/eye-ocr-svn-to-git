package eyedev._06;

import prophecy.common.image.BWImage;

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class AbstractLineSegmenter implements LineSegmenter {
  protected BWImage image;
  protected List<Rectangle> rectangles = new ArrayList<Rectangle>();

  public AbstractLineSegmenter(BWImage image) {
    this.image = image;
  }

  public abstract void go();

  protected void addLetter(int x1, int x2) {
    int y1 = 0, y2 = image.getHeight();
    while (isWhiteLine(y1, x1, x2))
      ++y1;
    while (isWhiteLine(y2-1, x1, x2))
      --y2;
    rectangles.add(new Rectangle(x1, y1, x2-x1, y2-y1));
  }

  public java.util.List<Rectangle> getRectangles() {
    return rectangles;
  }

  protected boolean isWhiteColumn(int x) {
    for (int y = 0; y < image.getHeight(); y++)
      if (image.getPixel(x, y) < .5f)
        return false;
    return true;
  }

  private boolean isWhiteLine(int y, int x1, int x2) {
    for (int x = x1; x < x2; x++)
      if (image.getPixel(x, y) < .5f)
        return false;
    return true;
  }

  public void printRectangles() {
    for (Rectangle rectangle : rectangles) {
      System.out.println("segment: " + rectangle);
    }
  }
}
