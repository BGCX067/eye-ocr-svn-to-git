package eyedev._01;

import drjava.util.Tree;
import prophecy.common.image.BWImage;

public class CVersusT extends ImageReader {
  public String readImage(BWImage image) {
    return image.getPixel(0, 1) == 0.0f ? "C" : "T";
  }

  public Tree toTree() {
    return OCRUtil.treeFor(this);
  }

  public void fromTree(Tree tree) {
  }
}
