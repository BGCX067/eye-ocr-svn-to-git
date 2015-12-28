package eyedev._10;

import eye.eye01.TextPainter2;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageSurface;
import prophecy.common.image.RGB;
import prophecy.common.image.RGBImage;

import java.awt.*;

public class MovingCircleTest {
  private BWImage image;
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

  public static void main(String[] args) {
    new MovingCircleTest();
  }

  MovingCircleTest() {
    image = new TextPainter2(new Font("Arial", Font.BOLD, 50)).makeImage("J", 10);
    w = image.getWidth();
    h = image.getHeight();
    circleSize = 1;
    while (circleSize < Math.min(w, h)) {
      prepare();
      scan();
      boolean hasBlobs = hasBlobs();
      System.out.println("circle size: " + circleSize + ". has blobs: " + hasBlobs);
      if (!hasBlobs) break;
      ++circleSize;
    }
    showMarkedImage();
  }

  private void prepare() {
    grid = new boolean[w][h];
    workingImage = new BWImage(image);
  }

  private void showMarkedImage() {
    RGBImage markedImage = image.toRGB();
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        if (grid[x][y])
          markedImage.setPixel(x+circleSize/2, y+circleSize/2, new RGB(Color.red));
    ImageSurface imageSurface = new ImageSurface(markedImage);
    imageSurface.setZoom(2.0);
    SurfaceUtil.showAsMain("J", imageSurface);
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
    int c = contact(x, y), c2;
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
    int bestValue = 0;

    for (int i = 0; i < 8; i++) {
      Point p = new Point(x+directions[i].x, y+directions[i].y);
      int value = contact(p.x, p.y);
      if (value > bestValue) {
        best = p;
        bestValue = value;
      }
    }

    return best;
  }

  private int contact(int x1, int y1) {
    int pixels = 0;
    for (int y = 0; y < Math.min(circleSize, h-y1); y++)
      for (int x = 0; x < Math.min(circleSize, w-x1); x++) {
        if (x1+x >= 0 && x1+x < w && y1+y >= 0 && y1+y < h) {
          if (workingImage.getPixel(x1+x, y1+y) < 1f)
            ++pixels;
        }
      }
    return pixels;
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
