package eyedev._09;

import drjava.util.Tree;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageProcessing;

import java.awt.*;
import java.util.List;

public class WithAdvancedSegmenter extends ImageReader {
  private Tree characterRecognizer;

  public WithAdvancedSegmenter() {
  }

  public WithAdvancedSegmenter(String characterRecognizerDesc) {
    characterRecognizer = Tree.parse(characterRecognizerDesc);
  }

  public String readImage(BWImage image) {
    image = ImageProcessing.threshold(image, .5f);

    MultiLineSegmenter segmenter = new MultiLineSegmenter(image);
    segmenter.go();

    ImageReader recognizer = OCRUtil.makeImageReader(characterRecognizer);

    StringBuffer buf = new StringBuffer();
    for (List<Rectangle> line : segmenter.getLines()) {
      if (buf.length() != 0) buf.append("\n");
      for (int i = 0; i < line.size(); i++) {
        Rectangle r = line.get(i);
        if (i > 0 && isSpace(line.get(i-1), r))
          buf.append(" ");

        BWImage clip = image.clip(r);
        String text = recognizer.readImage(clip);
        if (text == null) text = "?";
        buf.append(text);
      }
    }
    return buf.toString();
  }

  private boolean isSpace(Rectangle leftChar, Rectangle rightChar) {
    int h = Math.max(leftChar.height, rightChar.height);
    int distance = rightChar.x-(leftChar.x+leftChar.width);
    float threshold = h/3;
    //System.out.println("threshold: " + threshold + ", distance: " + distance);
    return distance >= threshold;
  }

  public void fromTree(Tree tree) {
    characterRecognizer = tree.get(0);
  }

  public Tree toTree() {
    return OCRUtil.treeFor(this).add(characterRecognizer);
  }
}
