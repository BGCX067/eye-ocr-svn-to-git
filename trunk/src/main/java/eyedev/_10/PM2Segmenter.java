package eyedev._10;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRImageUtil;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;

import java.awt.*;
import java.util.Iterator;

public class PM2Segmenter extends ImageReader {
  PM2 pm;

  public PM2Segmenter(PM2 pm) {
    this.pm = pm;
  }

  public PM2Segmenter() {
  }

  public String readImage(BWImage image) {
    image = OCRImageUtil.trim(image);
    
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

        //System.out.println("item_y1=" + item_y1 + ", img_y1="+img_y1);
        float missing = pm.erase(item.codedImage, image2, 0, img_y1-item_y1);
        //System.out.println(item.text + " missing: " + missing);
        if (missing <= 0.1f) {
          int left = OCRImageUtil.croppableLeft(image2);
          float ratio = (float) left/pm.width(item.codedImage);
          //System.out.println("ratio: " + ratio);
          if (ratio >= 0.9f) {
            //System.out.println("found: " + item.text);
            buf.append(item.text);
            image = OCRImageUtil.trim(image2);
            continue loop;
          }
        }
      }
      break;
    }

    return buf.toString();
  }

  public void fromTree(Tree tree) {
    pm = new PM2();
    pm.fromTree(tree);
  }

  public Tree toTree() {
    return pm.toTree().setName(OCRUtil.treeFor(this).getName());
  }
}
