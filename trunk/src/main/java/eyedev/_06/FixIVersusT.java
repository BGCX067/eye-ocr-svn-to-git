package eyedev._06;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;

public class FixIVersusT extends ImageReader {
  protected ImageReader baseReader;

  public FixIVersusT() {
  }

  public FixIVersusT(ImageReader baseReader) {
    this.baseReader = baseReader;
  }

  public String readImage(BWImage image) {
    String text = baseReader.readImage(image);
    if ("I".equals(text) || "T".equals(text)) {
      float ratio = (float) image.getWidth() / image.getHeight();
      float threshold = 0.5f;
      //System.out.println("Ratio:" + ratio);
      text = ratio >= threshold ? "T" : "I";
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
