package eyedev._09;

import eyedev._06.LineSegmenter;
import eyedev._06.SimpleSegmenter;
import prophecy.common.image.BWImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MultiLineSegmenter {
  private BWImage image;
  private List<List<Rectangle>> lines = new ArrayList<List<Rectangle>>();

  public MultiLineSegmenter(BWImage image) {
    this.image = image;
  }

  public void go() {
    boolean lastWhite = true;
    int lineStart = 0;
    lines.clear();

    for (int y = 0; y < image.getHeight(); y++) {
      boolean white = isWhiteRow(y, 0, image.getWidth());
      if (!white && lastWhite) {
        lineStart = y;
      } else if (white && !lastWhite) {
        addLine(lineStart, y);
      }
      lastWhite = white;
    }
    if (!lastWhite)
      addLine(lineStart, image.getHeight());
  }

  private void addLine(int y1, int y2) {
    BWImage clip = image.clip(0, y1, image.getWidth(), y2 - y1);
    LineSegmenter lineSegmenter;
    lineSegmenter = new SimpleSegmenter(clip);
    lineSegmenter.go();
    List<Rectangle> rectangles = new ArrayList<Rectangle>();
    for (Rectangle r : lineSegmenter.getRectangles())
      rectangles.add(new Rectangle(r.x, r.y+y1, r.width, r.height));
    lines.add(rectangles);
  }

  public List<List<Rectangle>> getLines() {
    return lines;
  }

  private boolean isWhiteRow(int y, int x1, int x2) {
    for (int x = x1; x < x2; x++)
      if (image.getPixel(x, y) < .5f)
        return false;
    return true;
  }
}
