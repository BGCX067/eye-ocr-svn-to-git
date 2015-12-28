package eyedev._06;

import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import eyedev._05.TextPainter;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageSurface;

import java.awt.*;

public class SegmentationTest {
  public static void main(String[] args) {
    // sizes work from 15 to 110
    int size = 110;
    Font font = new Font("Arial", Font.BOLD, size);
    BWImage image = TextPainter.paintText(1000, 120, 10, 110, font, "DONNIE DARKO");
    //SurfaceUtil.showAsMain("DONNIE", new ImageSurface(image));

    segmentAndRecognize(image);

    showMarkedImage(image);
  }

  private static void showMarkedImage(BWImage image) {
    SimpleSegmenter segmenter = new SimpleSegmenter(image);
    segmenter.go();
    BWImage markedImage = new BWImage(image);
    for (Rectangle r : segmenter.getRectangles()) {
      drawBox(markedImage, r.x, r.y, r.width-1, r.height-1);
    }
    SurfaceUtil.showAsMain("DONNIE", new ImageSurface(markedImage));
  }

  private static void segmentAndRecognize(BWImage image) {
    SimpleSegmenter segmenter = new SimpleSegmenter(image);
    segmenter.go();

    ImageReader recognizer = Recognizer06.makeRecognizer();
    System.out.println(OCRUtil.getImageReaderDescription(recognizer));

    StringBuffer buf = new StringBuffer();
    for (Rectangle r : segmenter.getRectangles()) {
      BWImage clip = image.clip(r);
      String text = recognizer.readImage(clip);
      if (text == null) text = "?";
      buf.append(text);
    }
    String text = buf.toString();
    System.out.println("Recognized text: " + text);
  }

  private static void drawBox(BWImage image, int x1, int y1, int w, int h) {
    for (int x = 0; x < w; x++) {
      drawBoxPixel(image, x1+x, y1);
      drawBoxPixel(image, x1+x, y1+h);
    }

    for (int y = 0; y < h; y++) {
      drawBoxPixel(image, x1, y1+y);
      drawBoxPixel(image, x1+w, y1+y);
    }
  }

  private static void drawBoxPixel(BWImage image, int x, int y) {
    if (image.getPixel(x, y) == 1f)
      image.setPixel(x, y, 0.75f);
  }
}
