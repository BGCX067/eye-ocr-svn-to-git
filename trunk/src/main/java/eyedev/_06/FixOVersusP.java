package eyedev._06;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;

public class FixOVersusP extends ImageReader {
  private ImageReader baseReader;

  public FixOVersusP() {
  }

  public FixOVersusP(ImageReader baseReader) {
    this.baseReader = baseReader;
  }

  public String readImage(BWImage image) {
    String text = baseReader.readImage(image);
    if ("O".equals(text) || "P".equals(text))
      text = image.getPixel(0, 1) == 0f ? "P" : "O";
    return text;
  }

  public Tree toTree() {
    return OCRUtil.treeFor(this).add(baseReader.toTree());
  }

  public void fromTree(Tree tree) {
    baseReader = (ImageReader) OCRUtil.fromTree(tree.get(0));
  }
}
