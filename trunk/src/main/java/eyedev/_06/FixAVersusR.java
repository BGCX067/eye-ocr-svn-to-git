package eyedev._06;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;

public class FixAVersusR extends ImageReader {
  protected ImageReader baseReader;

  public FixAVersusR() {
  }

  public FixAVersusR(ImageReader baseReader) {
    this.baseReader = baseReader;
  }

  public String readImage(BWImage image) {
    String text = baseReader.readImage(image);
    if ("A".equals(text) || "R".equals(text)) {
      float leftishness = RecogUtil.leftishness(image);
      text = leftishness >= 0.05f ? "R" : "A";
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