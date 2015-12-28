package eyedev._06;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;

public class FixSVersusG extends ImageReader {
  private ImageReader baseReader;

  public FixSVersusG() {
  }

  public FixSVersusG(ImageReader baseReader) {
    this.baseReader = baseReader;
  }

  public String readImage(BWImage image) {
    String text = baseReader.readImage(image);
    if ("S".equals(text) || "G".equals(text)) {
      float width = image.getWidth();
      int middleY = (int) (image.getHeight() / 2f);
      float middle = RecogUtil.getAverageWidth(image, middleY, middleY+1);
      float ratio = middle/width;
      //System.out.println("s-versus-g ratio: " + ratio);
      text = ratio < 0.9f ? "S" : "G";
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
