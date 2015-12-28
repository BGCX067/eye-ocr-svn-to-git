package eyedev._09;

import drjava.util.Tree;
import eyedev._01.*;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageProcessing;

import java.awt.*;
import java.util.List;

public class WithFlexibleSegmenter extends ImageReader {
  private Tree characterRecognizer;

  public WithFlexibleSegmenter() {
  }

  public WithFlexibleSegmenter(String characterRecognizerDesc) {
    characterRecognizer = Tree.parse(characterRecognizerDesc);
  }

  public String readImage(BWImage image) {
    image = ImageProcessing.threshold(image, .5f);

    List<SeparateTheLines.Line> lines = new SeparateTheLines(image).getLines();

    ImageReader recognizer = OCRUtil.makeImageReader(characterRecognizer);

    StringBuffer buf = new StringBuffer();
    for (SeparateTheLines.Line line : lines) {
      if (buf.length() != 0) buf.append("\n");

      List<Segment> letters = new FlexibleSegmenter().segment(image);

      for (int i = 0; i < letters.size(); i++) {
        if (i > 0 && isSpace(letters.get(i-1).boundingBox, letters.get(i).boundingBox))
          buf.append(" ");

        Segment letter = letters.get(i);
        RecognizedText text = recognizer.extendedReadImage(new InputImage(letter.segmentImage));

        if (collectDebugInfo)
          addDebugItem(new DebugItem("Letter " + (i+1),
            new Subrecognition(letter.boundingBox, letter.segmentImage, characterRecognizer.toString(), text)));

        buf.append(text == null || text.text == null ? "?" : text.text);
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
    return OCRUtil.treeFor(this)
      .add(characterRecognizer);
  }
}
