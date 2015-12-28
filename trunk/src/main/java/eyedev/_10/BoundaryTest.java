package eyedev._10;

import eye.eye01.TextPainter2;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageProcessing;
import prophecy.common.image.ImageSurface;

import java.awt.*;

public class BoundaryTest {
  private BWImage baseImage;
  private static int w, h;
  private BWImage boundaryImage;

  private static final boolean doubleSize = true;

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
    new BoundaryTest();
  }

  BoundaryTest() {
    baseImage = new TextPainter2(new Font("Arial", Font.BOLD, 50)).makeImage("Kirp!", 0);
    baseImage = ImageProcessing.addBorder(baseImage, 1);

    if (doubleSize)
      baseImage = ImageProcessing.resize(baseImage, baseImage.getWidth()*2, baseImage.getHeight()*2);

    w = baseImage.getWidth();
    h = baseImage.getHeight();

    makeBoundary();

    ImageSurface imageSurface = new ImageSurface(boundaryImage);
    imageSurface.setZoom(doubleSize ? 2.0 : 4.0);
    SurfaceUtil.showAsMain("BoundaryTest", imageSurface);
  }

  private void makeBoundary() {
    boundaryImage = new BWImage(w, h, 1f);
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++) {
        if (isBoundaryPixel2(x, y))
          boundaryImage.setPixel(x, y, 0f);
      }
  }

  // not so good - makes lines 2 pixel wide
  private boolean isBoundaryPixel1(int x, int y) {
    for (int i = 0; i < 8; i++) {
      Point p = new Point(x+directions[i].x, y+directions[i].y);
      float f = getPixel(p);
      if (f != baseImage.getPixel(x, y))
        return true;
    }
    return false;
  }

  // this is a good approach
  private boolean isBoundaryPixel2(int x, int y) {
    Point[] directions = {
      new Point(1, 0),
      new Point(0, 1),
      new Point(1, 1)
    };

    return isBoundaryPixel(x, y, directions);
  }

  // not so good - not always symmetrical in x (probably not in y either)
  private boolean isBoundaryPixel3(int x, int y) {
    Point[] directions = {
      new Point(1, 0),
      new Point(0, 1)
    };

    return isBoundaryPixel(x, y, directions);
  }

  // like v. 2, but we're trying to move the boundary pixel towards the white area
  // this is better for narrow lines in the original image as they don't lead to a blob
  // (maybe not so good if there are very narrow white spaces in the image though)
  private boolean isBoundaryPixel4(int x, int y) {
    // only white pixels can be boundary pixels
    if (getPixel(x, y) != 1f) return false;

    return isBoundaryPixel1(x, y);
  }

  private boolean isBoundaryPixel(int x, int y, Point[] directions) {
    for (int i = 0; i < directions.length; i++) {
      Point p = new Point(x+directions[i].x, y+directions[i].y);
      float f = getPixel(p);
      if (f != baseImage.getPixel(x, y))
        return true;
    }
    return false;
  }

  private float getPixel(Point p) {
    return getPixel(p.x, p.y);
  }

  private float getPixel(int x, int y) {
    return x >= 0 && x < w && y >= 0 && y < h ? baseImage.getPixel(x, y) : 1f;
  }
}
