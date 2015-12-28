package eyedev._11;

import eye.eye01.TextPainter2;
import eye.eye02.FontFinder;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.*;

import javax.swing.*;
import java.awt.*;

public class FlexibleSegmentationVisualizer {
  public static class FlexibleSegmentationOptions {
    public float threshold = 0.5f;
  }

  public static void show(BWImage image) {
    show(image, new FlexibleSegmentationOptions());
  }

  public static void show(BWImage image, FlexibleSegmentationOptions options) {
    image = ImageProcessing.threshold(image, options.threshold);
    BWImage baseImage = ImageProcessing.addBorder(image, 1);
    RGBImage markedImage = baseImage.toRGB();
    segment(baseImage, markedImage);
    ImageSurface imageSurface = new ImageSurface(markedImage);
    imageSurface.setZoom(2.0);
    SurfaceUtil.show("Flexible segmentation", new JScrollPane(imageSurface));
  }

  // we always want an inset of at least 1 for this
  private static void segment(BWImage baseImage, RGBImage markedImage) {
    int w = baseImage.getWidth(), h = baseImage.getHeight();
    int lastSep = 0;
    xloop: for (int x1 = 0; x1 < w; x1++) {
      int x = x1;
      for (int y = 0; y < h-1; y++) {
        markedImage.setPixel(x, y, new RGB(Color.red));

        // The parts right of the && are so we don't step diagonally over 1-pixel wide lines
        boolean l = baseImage.getPixel(x-1, y+1) == 1f && baseImage.getPixel(x-1, y) == 1f;
        boolean m = baseImage.getPixel(x, y+1) == 1f;
        boolean r = baseImage.getPixel(x+1, y+1) == 1f && baseImage.getPixel(x+1, y) == 1f;
        
        if (m) {
          // ok, proceed downwards
        } else if (l) {
          --x;
        } else if (r) {
          ++x;
        } else
          continue xloop;
      }
      /*if (x > lastSep+1) {
        System.out.println("separator found: " + x);
      }*/
      lastSep = x;
    }
  }
}
