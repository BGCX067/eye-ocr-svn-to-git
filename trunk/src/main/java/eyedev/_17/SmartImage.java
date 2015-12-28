package eyedev._17;

import prophecy.common.image.BWImage;
import prophecy.common.image.RGB;
import prophecy.common.image.RGBImage;

import java.awt.image.BufferedImage;

/** an image that is stored either in color or b/w (if possible) */
public class SmartImage {
  RGBImage colorImage;
  BWImage bwImage;

  public RGBImage getRGBImage() {
    return colorImage != null ? colorImage : bwImage.toRGB();
  }

  public BWImage getBWImage() {
    return colorImage != null ? colorImage.toBW() : bwImage;
  }

  public SmartImage(BufferedImage bufferedImage) {
    RGBImage image = new RGBImage(bufferedImage);
    if (isBW(image)) {
      System.out.println("image is b/w");
      bwImage = image.toBW();
    } else
      colorImage = image;
  }

  public SmartImage(RGBImage image) {
    colorImage = image;
  }

  private boolean isBW(RGBImage image) {
    int width = image.getWidth(), height = image.getHeight();
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++)
        if (!isBW(image.getPixel(x, y)))
          return false;
    return true;
  }

  private boolean isBW(RGB rgb) {
    return rgb.r == rgb.g && rgb.g == rgb.b;
  }

  public int getWidth() {
    return colorImage != null ? colorImage.getWidth() : bwImage.getWidth();
  }

  public int getHeight() {
    return colorImage != null ? colorImage.getHeight() : bwImage.getHeight();
  }
}
