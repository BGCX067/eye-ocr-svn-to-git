package eyedev._01;

import eye.eye01.ScrollableImage;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.BWImage;
import prophecy.common.image.RGB;
import prophecy.common.image.RGBImage;

import java.awt.*;

public class OCRImageUtil {
  public static BWImage makeImage(String... lines) {
    int width = lines[0].length();
    int height = lines.length;
    BWImage img = new BWImage(width, height, 0.0f);
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++)
        img.setPixel(x, y, lines[y].charAt(x) != ' ' ? 0.0f : 1.0f);
    return img;
  }

  public static boolean isAllWhite(BWImage image) {
    for (int y = 0; y < image.getHeight(); y++)
      for (int x = 0; x < image.getWidth(); x++)
        if (image.getPixel(x, y) != 1f)
          return false;
    return true;
  }

  public static BWImage trim(BWImage image) {
    int w = image.getWidth(), h = image.getHeight();
    int x1 = w, x2 = 0, y1 = h, y2 = 0;
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        if (image.getPixel(x, y) != 1f) {
          x1 = Math.min(x1, x);
          x2 = Math.max(x2, x);
          y1 = Math.min(y1, y);
          y2 = Math.max(y2, y);
        }
    //System.out.println("clip: " + x1 + " " + y1 + " " + x2 + " " + y2);
    if (x1 == 0 && y1 == 0 && x2 == w-1 && y2 == h-1)
      return image;
    else {
      if (x2 < x1 || y2 < y1)
        return new BWImage(0, 0, 1f);
      else
        return image.clip(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
    }
  }

  public static int croppableLeft(BWImage image) {
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++)
        if (image.getPixel(x, y) != 1f)
          return x;
    }
    return image.getWidth();
  }

  public static float minBrightness(BWImage img) {
    float result = 1f;
    for (int y = 0; y < img.getHeight(); y++)
      for (int x = 0; x < img.getWidth(); x++)
        result = Math.min(result, img.getPixel(x, y));
    return result;
  }

  public static float maxBrightness(BWImage img) {
    float result = 0f;
    for (int y = 0; y < img.getHeight(); y++)
      for (int x = 0; x < img.getWidth(); x++)
        result = Math.max(result, img.getPixel(x, y));
    return result;
  }

  public static Rectangle getBoundingBox(BWImage image) {
    int w = image.getWidth(), h = image.getHeight();
    int x1 = w, x2 = 0, y1 = h, y2 = 0;
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        if (image.getPixel(x, y) != 1f) {
          x1 = Math.min(x1, x);
          x2 = Math.max(x2, x);
          y1 = Math.min(y1, y);
          y2 = Math.max(y2, y);
        }
    return new Rectangle(x1, y1, Math.max(0, x2 - x1 + 1), Math.max(0, y2 - y1 + 1));
  }

  /** compares two images (both having the same size) pixel by pixel
   *  and returns a similarity measure (between 0 and 1) 
   */
  public static float similaritySameSize(BWImage image1, BWImage image2) {
    int w = image1.getWidth(), h = image1.getHeight();
    double sum = 0;
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        sum += Math.abs(image1.getPixel(x, y)-image2.getPixel(x, y));
    return (float) (1-sum/(w*h));
  }

  public static BWImage multiply(BWImage image1, BWImage image2) {
    int w = image1.getWidth(), h = image1.getHeight();
    BWImage result = new BWImage(w, h, 0f);
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        result.setPixel(x, y, image1.getPixel(x, y)*image2.getPixel(x, y));
    return result;
  }

  public static int numPixelsDarkerThan(BWImage img, float threshold) {
    int w = img.getWidth(), h = img.getHeight();
    int count = 0;
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        if (img.getPixel(x, y) < threshold)
          ++count;
    return count;
  }

  public static int numPixelsBrighterThan(BWImage img, float threshold) {
    int w = img.getWidth(), h = img.getHeight();
    return w*h-numPixelsDarkerThan(img, threshold);      
  }

  public static void fillBackground(RGBImage image, int x1, int y1, int w, int h, RGB fillColor) {
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++) {
        RGB rgb = image.getPixel(x1+x, y1+y);
        if (rgb.getBrightness() >= fillColor.getBrightness())
          image.setPixel(x1+x, y1+y, fillColor);
      }
  }

  public static BWImage rotateCounterClockwise(BWImage image) {
    int w = image.getWidth(), h = image.getHeight();
    BWImage result = new BWImage(h, w, 0f);
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        result.setPixel(y, x, image.getPixel(w-1-x, y));
    return result;
  }

  public static void show(RGBImage image) {
    SurfaceUtil.show(new ScrollableImage(image));
  }
}
