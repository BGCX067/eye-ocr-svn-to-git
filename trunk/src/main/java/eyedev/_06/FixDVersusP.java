package eyedev._06;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;

public class FixDVersusP extends ImageReader {
  protected ImageReader baseReader;

  public FixDVersusP() {
  }

  public FixDVersusP(ImageReader baseReader) {
    this.baseReader = baseReader;
  }

  public String readImage(BWImage image) {
    String text = baseReader.readImage(image);
    if ("D".equals(text) || "P".equals(text)) {
      float upper = RecogUtil.getAverageWidth(image, 0, (int) (image.getHeight()/3f));
      float lower = RecogUtil.getAverageWidth(image, (int) (image.getHeight()*2/3f), image.getHeight());
      float ratio = lower/upper;
      //System.out.println("upper: " + upper + ", lower: " + lower + ", ratio: " + ratio);
      text = ratio < 0.75f ? "P" : "D";
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
