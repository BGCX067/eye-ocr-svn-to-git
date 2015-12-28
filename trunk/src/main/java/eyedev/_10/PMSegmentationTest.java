package eyedev._10;

import eyedev._01.ACT_ExampleSet;
import eyedev._01.ExampleSet;
import eyedev._01.MiniLetters;
import eyedev._01.OCRImageUtil;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageProcessing;

import java.awt.*;
import java.util.Iterator;

public class PMSegmentationTest {
  public static void main(String[] args) {
    ExampleSet exampleSet = new ACT_ExampleSet();

    int spacing = 0, y1 = 0;
    BWImage image = new BWImage(10, 6, 1f);
    ImageProcessing.copy(MiniLetters.a(), 0, 0, image, 0, y1, 5, 5);
    ImageProcessing.copy(MiniLetters.c(), 0, 0, image, 5+spacing, y1, 5, 5);
    //SurfaceUtil.showAsMain("", new ImageSurface(image));

    PM2 pm = new PM2(exampleSet);
    StringBuffer buf = new StringBuffer();

    loop: while (true) {
      for (PM2.Item item : pm.items) {
        BWImage image2 = new BWImage(image);

        int item_y1 = 0;
        Iterator<Point> it = pm.blackPixels(item.codedImage);
        while (it.hasNext()) {
          Point p = it.next();
          if (p.x == 0) {
            item_y1 = p.y;
            break;
          }
        }

        int img_y1 = 0;
        while (img_y1 < image2.getHeight() && image2.getPixel(0, img_y1) == 1f)
          ++img_y1;

        System.out.println("item_y1=" + item_y1 + ", img_y1="+img_y1);
        float missing = pm.erase(item.codedImage, image2, 0, img_y1-item_y1);
        System.out.println(item.text + " missing: " + missing);
        if (missing <= 0.1f) {
          int left = croppableLeft(image2);
          float ratio = (float) left/pm.width(item.codedImage);
          System.out.println("ratio: " + ratio);
          if (ratio >= 0.9f) {
            System.out.println("found: " + item.text);
            buf.append(item.text);
            image = OCRImageUtil.trim(image2);
            continue loop;
          }
        }
      }
      break;
    }

    System.out.println("Text: " + buf);
  }

  private static int croppableLeft(BWImage image) {
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++)
        if (image.getPixel(x, y) != 1f)
          return x;
    }
    return image.getWidth();
  }
}
