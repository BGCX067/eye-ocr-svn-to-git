package eyedev._04;

import drjava.util.Tree;
import eyedev._01.OCRImageUtil;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;

public class Crop implements Simplifier {
  public BWImage simplify(BWImage image) {
    return OCRImageUtil.trim(image);
  }

  public Tree toTree() {
    return OCRUtil.treeFor(this);
  }
  
  public void fromTree(Tree tree) {
  }
}
