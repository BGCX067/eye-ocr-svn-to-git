package eyedev._10;

import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import eye.eye01.ScrollableImage;
import eye.eye01.TextPainter2;
import prophecy.common.image.BWImage;
import prophecy.common.image.RGB;
import prophecy.common.image.RGBImage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class MovingCircleTest2 {
  private BWImage baseImage;
  private int w, h;
  private int circleSize;
  private boolean[][] grid;
  private BWImage workingImage;

  static final Point[] directions = {
    new Point(-1, -1),
    new Point(0, -1),
    new Point(1, -1),
    new Point(-1, 0),
    new Point(1, 0),
    new Point(-1, 1),
    new Point(0, 1),
    new Point(1, 1),
  };
  private JSlider slider;
  private ScrollableImage scrollableImage;

  public static void main(String[] args) {
    new MovingCircleTest2();
  }

  MovingCircleTest2() {
    JFrame frame = new JFrame("MovingCircleTest2");
    frame.setSize(400, 400);
    GUIUtil.centerOnScreen(frame);

    baseImage = new TextPainter2(new Font("Arial", Font.BOLD, 50)).makeImage("J", 10);
    w = baseImage.getWidth();
    h = baseImage.getHeight();

    slider = new JSlider(1, 20, 1);
    scrollableImage = new ScrollableImage(baseImage.toRGB());
    scrollableImage.setZoom(2.0);

    slider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent changeEvent) {
        int value = slider.getValue();
        if (value != circleSize) {
          circleSize = value;
          go();
        }
      }
    });

    frame.getContentPane().setLayout(new LetterLayout("S", "I", "I"));
    frame.getContentPane().add("S", GUIUtil.withLabel("Circle size:", slider));
    frame.getContentPane().add("I", scrollableImage);

    GUIUtil.showMainFrame(frame);

    /*circleSize = 1;
    while (circleSize < Math.min(w, h)) {
      prepare();
      scan();
      boolean hasBlobs = hasBlobs();
      System.out.println("circle size: " + circleSize + ". has blobs: " + hasBlobs);
      if (!hasBlobs) break;
      ++circleSize;
    }
    showMarkedImage();*/
  }

  private void go() {
    prepare();
    scan();
    showMarkedImage();
  }

  private void prepare() {
    grid = new boolean[w][h];
    workingImage = new BWImage(baseImage);
  }

  private void showMarkedImage() {
    RGBImage markedImage = baseImage.toRGB();
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        if (grid[x][y])
          markedImage.setPixel(x+circleSize/2, y+circleSize/2, new RGB(Color.red));

    scrollableImage.setImage(markedImage);
  }

  private void scan() {
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++) {
        if (contact(x, y) != 0) {
          Point p = adjustStartingPoint(x, y);
          System.out.println("Starting point: " + p + " (from " + x + ", " + y + ")");
          explore(p.x, p.y);
        }
        //System.out.println("Structure found: " + x + "/" + y);
      }
  }

  private Point adjustStartingPoint(int x, int y) {
    long c = contact(x, y), c2;
    while (true) {
      c2 = contact(x+1, y);
      if (c2 > c) {
        x = x+1;
        c = c2;
        continue;
      }

      c2 = contact(x, y+1);
      if (c2 > c) {
        y = y+1;
        c = c2;
        continue;
      }

      break;
    }

    return new Point(x, y);
  }

  private void explore(int x, int y) {
    if (eraseCircle(x, y)) {
      while (true) {
        Point p = findBestDirection(x, y);
        if (p == null)
          break;
        explore(p.x, p.y);
      }
    }
  }

  private Point findBestDirection(int x, int y) {
    Point best = null;
    long bestValue = 0;

    for (int i = 0; i < 8; i++) {
      Point p = new Point(x+directions[i].x, y+directions[i].y);
      long value = contact(p.x, p.y);
      if (value > bestValue) {
        best = p;
        bestValue = value;
      }
    }

    return best;
  }

  private long contact(int x1, int y1) {
    long contactCount = 0;
    for (int y = 0; y < Math.min(circleSize, h-y1); y++)
      for (int x = 0; x < Math.min(circleSize, w-x1); x++) {
        if (x1+x >= 0 && x1+x < w && y1+y >= 0 && y1+y < h) {
          if (workingImage.getPixel(x1+x, y1+y) < 1f) {
            int contactValue = 1+circleSize-Math.abs(x-circleSize/2)-Math.abs(y-circleSize/2);
            contactCount += contactValue;
          }
        }
      }
    return contactCount;
  }

  // erases a circle in the image. returns true if there was anything to erase
  // this circle currently is a rectangle :)
  private boolean eraseCircle(int x1, int y1) {
    if (!(x1 >= 0 && x1 < w && y1 >= 0 && y1 < h)) return false;
    
    boolean contact = false;
    for (int y = 0; y < Math.min(circleSize, h-y1); y++)
      for (int x = 0; x < Math.min(circleSize, w-x1); x++) {
        if (x1+x >= 0 && x1+x < w && y1+y >= 0 && y1+y < h) {
          if (workingImage.getPixel(x1+x, y1+y) < 1f) {
            workingImage.setPixel(x1+x, y1+y, 1f);
            contact = true;
          }
        }
      }
    if (contact)
      grid[x1][y1] = true;
    return contact;
  }

  private boolean hasBlobs() {
    int minBlobSize = 3;
    for (int y = 0; y <= h-minBlobSize; y++)
      for (int x = 0; x <= w-minBlobSize; x++) {
        if (isBlob(x, y, minBlobSize))
          return true;
      }
    return false;
  }

  private boolean isBlob(int x1, int y1, int size) {
    for (int y = 0; y < size; y++)
      for (int x = 0; x < size; x++)
        if (!grid[x1+x][y1+y])
          return false;
    return true;
  }

}
