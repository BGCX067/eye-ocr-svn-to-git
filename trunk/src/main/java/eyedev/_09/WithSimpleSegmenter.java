package eyedev._09;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import eyedev._06.SimpleSegmenter;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageProcessing;

import java.awt.*;

public class WithSimpleSegmenter extends ImageReader {
  private String characterRecognizerDesc;

  public WithSimpleSegmenter(String characterRecognizerDesc) {
    this.characterRecognizerDesc = characterRecognizerDesc;
  }

  public String readImage(BWImage image) {
    image = ImageProcessing.threshold(image, .5f);

    SimpleSegmenter segmenter = new SimpleSegmenter(image);
    segmenter.go();

    ImageReader recognizer = OCRUtil.makeImageReader(characterRecognizerDesc);

    StringBuffer buf = new StringBuffer();
    for (Rectangle r : segmenter.getRectangles()) {
      BWImage clip = image.clip(r);
      String text = recognizer.readImage(clip);
      if (text == null) text = "?";
      buf.append(text);
    }
    return buf.toString();
  }

  public void fromTree(Tree tree) {
    characterRecognizerDesc = tree.get(0).toString();
  }

  public Tree toTree() {
    return OCRUtil.treeFor(this)
      .add(Tree.parse(characterRecognizerDesc));
  }
}
