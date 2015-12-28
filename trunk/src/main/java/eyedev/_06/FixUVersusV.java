package eyedev._06;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;

public class FixUVersusV extends ImageReader {
  private ImageReader baseReader;

  public FixUVersusV() {
  }

  public FixUVersusV(ImageReader baseReader) {
    this.baseReader = baseReader;
  }

  public String readImage(BWImage image) {
    String text = baseReader.readImage(image);
    if ("U".equals(text) || "V".equals(text)) {
      float upper = RecogUtil.getAverageWidth(image, 0, (int) (image.getHeight()/3f));
      float middle = RecogUtil.getAverageWidth(image, (int) (image.getHeight()/3f), (int) (image.getHeight()*2/3f));
      float ratio = middle/upper;
      //System.out.println("u-versus-v ratio: " + ratio);
      text = ratio < 0.9f ? "V" : "U";
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
