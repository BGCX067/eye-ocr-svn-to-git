package eyedev._11;

import eye.eye01.TextPainter2;
import eye.eye02.FontFinder;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageSurface;
import prophecy.common.image.RGB;
import prophecy.common.image.RGBImage;

import java.awt.*;

public class FlexibleSegmentTest {
  public static void main(String[] args) throws Exception {
    String text = "SSXVN";
    Font font = FontFinder.getEyeFont("Arial").loadFont(30f);
    BWImage baseImage = new TextPainter2(font).makeImage(text, 10);
    RGBImage markedImage = baseImage.toRGB();
    segment(baseImage, markedImage);
    SurfaceUtil.showAsMain("FlexibleSegment", new ImageSurface(markedImage));
  }

  // we always want an inset of at least 1 for this
  private static void segment(BWImage baseImage, RGBImage markedImage) {
    int w = baseImage.getWidth(), h = baseImage.getHeight();
    int lastSep = 0;
    xloop: for (int x1 = 0; x1 < w; x1++) {
      int x = x1;
      for (int y = 0; y < h-1; y++) {
        markedImage.setPixel(x, y, new RGB(Color.red));
        boolean l = baseImage.getPixel(x-1, y+1) == 1f;
        boolean m = baseImage.getPixel(x, y+1) == 1f;
        boolean r = baseImage.getPixel(x+1, y+1) == 1f;
        if (m) {
          // ok, proceed downwards
        } else if (l) {
          --x;
        } else if (r) {
          ++x;
        } else
          continue xloop;
      }
      if (x > lastSep+1) {
        System.out.println("separator found: " + x);
      }
      lastSep = x;
    }
  }
}
