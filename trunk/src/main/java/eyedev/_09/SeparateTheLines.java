package eyedev._09;

import prophecy.common.image.BWImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeparateTheLines {
  public static class Line {
    public Rectangle clip;

    public Line(Rectangle clip) {
      this.clip = clip;
    }

    public Rectangle getRectangle() {
      return clip;
    }
  }

  private BWImage image;
  private List<Line> lines = new ArrayList<Line>();

  public SeparateTheLines(BWImage image) {
    this.image = image;
  }

  private void addLine(int y1, int y2) {
    lines.add(new Line(new Rectangle(0, y1, image.getWidth(), y2-y1)));
  }

  public List<Line> getLines() {
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
    return lines;
  }

  private boolean isWhiteRow(int y, int x1, int x2) {
    for (int x = x1; x < x2; x++)
      if (image.getPixel(x, y) < .5f)
        return false;
    return true;
  }
}
