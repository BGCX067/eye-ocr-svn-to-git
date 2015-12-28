package eyedev._14;

import prophecy.common.SurfaceUtil;
import prophecy.common.image.*;

import java.awt.*;

public class GrayscaleSegmentationVisualizer {
  public static void show(BWImage image) {
    BWImage baseImage = ImageProcessing.addBorder(image, 1);
    RGBImage markedImage = baseImage.toRGB();
    segment(baseImage, markedImage);
    ImageSurface imageSurface = new ImageSurface(markedImage);
    imageSurface.setZoom(2.0);
    SurfaceUtil.show("Grayscale segmentation", imageSurface);
  }

  // we always want an inset of at least 1 for this
  private static void segment(BWImage baseImage, RGBImage markedImage) {
    float whiteoutThreshold = 0.9f; // tolerance to account for JPEG artifacts or similar

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
