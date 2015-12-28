package eyedev._06;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;

public class FixVVersusY extends ImageReader {
  private ImageReader baseReader;

  public FixVVersusY() {
  }

  public FixVVersusY(ImageReader baseReader) {
    this.baseReader = baseReader;
  }

  public String readImage(BWImage image) {
    String text = baseReader.readImage(image);
    if ("V".equals(text) || "Y".equals(text)) {
      float upper = image.getWidth();
      float lower = RecogUtil.getAverageWidth(image, (int) (image.getHeight()/2f), image.getHeight());
      float ratio = lower/upper;
      //System.out.println("v-versus-y ratio: " + ratio);
      text = ratio < 0.3f ? "Y" : "V";
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
