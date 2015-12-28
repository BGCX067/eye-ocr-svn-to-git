package eyedev._06;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;

public class FixLVersusE extends ImageReader {
  protected ImageReader baseReader;

  public FixLVersusE() {
  }

  public FixLVersusE(ImageReader baseReader) {
    this.baseReader = baseReader;
  }

  public String readImage(BWImage image) {
    String text = baseReader.readImage(image);
    if ("L".equals(text) || "E".equals(text)) {
      float ratio = RecogUtil.upperWidthToLowerWidth(image);
      text = ratio < 0.75f ? "L" : "E";
    }
    return text;
  }

  public Tree toTree() {
    return OCRUtil.treeFor(this).add(baseReader.toTree());
  }

  public void fromTree(Tree tree) {
    baseReader = (ImageReader) OCRUtil.fromTree(tree.get(0));
  }
}
