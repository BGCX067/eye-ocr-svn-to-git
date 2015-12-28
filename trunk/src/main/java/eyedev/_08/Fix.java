package eyedev._08;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;

public class Fix extends ImageReader {
  private ImageReader baseImageReader;
  private float threshold;
  private String thresholdString; // float as string (to keep the shortened version)
  private String text1;
  private String text2;
  private int featureNr;

  public Fix() {}

  public Fix(int featureNr, String threshold, String text1, String text2, ImageReader baseImageReader) {
    this.featureNr = featureNr;
    thresholdString = threshold;
    this.threshold = Float.parseFloat(threshold);
    this.text1 = text1;
    this.text2 = text2;
    this.baseImageReader = baseImageReader;
  }

  public String readImage(BWImage image) {
    String text = baseImageReader.readImage(image);
    if (text1.equals(text) || text2.equals(text)) {
      float value = StandardFeatures.extractFeature(image, featureNr);
      text = value >= threshold ? text2 : text1;
    }
    return text;
  }

  public void fromTree(Tree tree) {
    text1 = tree.getUnquotedString(0);
    text2 = tree.getUnquotedString(1);
    featureNr = tree.getInt(2);
    thresholdString = tree.getUnquotedString(3);
    threshold = Float.parseFloat(thresholdString);
    baseImageReader = (ImageReader) OCRUtil.fromTree(tree.get(4));
  }

  public Tree toTree() {
    return OCRUtil.treeFor(this)
      .addUnquotedString(text1)
      .addUnquotedString(text2)
      .addInt(featureNr)
      .addUnquotedString(thresholdString)
      .add(baseImageReader.toTree());
  }
}
