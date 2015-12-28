package eyedev._12;

import prophecy.common.image.ImageProcessing;
import prophecy.common.image.RGB;
import prophecy.common.image.RGBImage;

import java.util.ArrayList;
import java.util.List;

public class LineRemover {
  public static void removeLines(RGBImage image) {
    for (int y = 1; y < image.getHeight()-1; y++) {
      int x1 = 1;
      while (x1 < image.getWidth()-1) {
        RGB lineColor = image.getPixel(x1, y);
        int x2 = x1+1;
        while (x2 < image.getWidth()-1 && colorDiff(image.getPixel(x2, y), lineColor) < 0.05)
          ++x2;
        int w = x2-x1;
        if (w >= 10) {
          RGB background = checkLineSurroundings(image, x1, w, y);
          if (background != null && colorDiff(lineColor, background) >= 0.2) {
            // remove line
            ImageProcessing.fillRect(image, x1, y, w, 1, background);
            /*System.out.println("Line removed: " + x1 + "/" + y + " (w=" + w + ") col=" + lineColor
              + " bg=" + background);*/
          }
        }
        x1 = x2;
      }
    }
  }

  static RGB checkLineSurroundings(RGBImage image, int x1, int w, int y) {
    List<RGB> pixels = getSurroundingPixels(image, x1, w, y);
    RGB color = average(pixels);
    if (allSimilar(pixels, color, 0.05))
      return color;
    return null;
  }

  static boolean allSimilar(List<RGB> pixels, RGB color, double threshold) {
    for (RGB pixel : pixels)
      if (colorDiff(pixel, color) >= threshold)
        return false;
    return true;
  }

  static RGB average(List<RGB> pixels) {
    double sumR = 0, sumG = 0, sumB = 0;
    for (RGB pixel : pixels) {
      sumR += pixel.r;
      sumG += pixel.g;
      sumB += pixel.b;
    }
    return new RGB(sumR/pixels.size(), sumG/pixels.size(), sumB/pixels.size());
  }

  static List<RGB> getSurroundingPixels(RGBImage image, int x1, int w, int y) {
    List<RGB> pixels = new ArrayList<RGB>();
    for (int x = x1; x < x1+w; x++) {
      pixels.add(image.getPixel(x, y-1));
      pixels.add(image.getPixel(x, y+1));
    }
    for (int yy = y-1; yy <= y+1; yy++) {
      pixels.add(image.getPixel(x1-1, yy));
      pixels.add(image.getPixel(x1+w, yy));
    }
    return pixels;
  }

  static float colorDiff(RGB col1, RGB col2) {
    return (Math.abs(col1.r-col2.r)+Math.abs(col1.g-col2.g)+Math.abs(col1.b-col2.b))/3f;
  }
}
