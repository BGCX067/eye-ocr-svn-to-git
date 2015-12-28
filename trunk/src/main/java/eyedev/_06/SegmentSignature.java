package eyedev._06;

import drjava.util.Tree;
import eyedev._01.Example;
import eyedev._01.ExampleSet;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import eyedev._05.SegmentCounter;
import prophecy.common.image.BWImage;

import java.util.*;

public class SegmentSignature extends ImageReader {
  private Map<String, String> sigToTextMap = new TreeMap<String, String>();

  public SegmentSignature() {}

  public SegmentSignature(ExampleSet exampleSet) {
    train(exampleSet);
  }

  public String readImage(BWImage image) {
    String sig = getSignature(image);
    //System.out.println("Signature: " + sig);
    return sigToTextMap.get(sig);
  }

  public void train(ExampleSet exampleSet) {
    for (Example example : exampleSet.examples) {
      String sig = getSignature(example.image);
      sigToTextMap.put(sig, example.text);
    }
  }

  private String getSignature(BWImage image) {
    return SegmentCounter.getXYSegmentSignature(image);
  }

  public Tree toTree() {
    Tree tExamples = new Tree();
    for (Map.Entry<String, String> e : sigToTextMap.entrySet()) {
      tExamples.addUnquotedString(e.getKey()).addUnquotedString(e.getValue());
    }
    return OCRUtil.treeFor(this).add(tExamples);
  }

  public void fromTree(Tree tree) {
    Tree tExamples = tree.get(0);
    for (int i = 0; i < tExamples.namelessChildrenCount(); i += 2) {
      sigToTextMap.put(tExamples.getUnquotedString(i), tExamples.getUnquotedString(i+1));
    }
  }
}
