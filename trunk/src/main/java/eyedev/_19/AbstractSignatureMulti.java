package eyedev._19;

import drjava.util.MultiMap;
import drjava.util.Tree;
import drjava.util.TreeUtil;
import eyedev._01.*;
import prophecy.common.image.BWImage;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractSignatureMulti extends ImageReader {
  private MultiMap<String, String> sigToTextMap = new MultiMap<String, String>();

  public String readImage(BWImage image) {
    String sig = getSignature(image);
    //System.out.println("Signature: " + sig);
    List<String> list = sigToTextMap.get(sig);
    return list.isEmpty() ? null : list.get(0);
  }

  public void train(ExampleSet exampleSet) {
    for (Example example : exampleSet.examples) {
      String sig = getSignature(example.image);
      if (!sigToTextMap.get(sig).contains(example.text))
        sigToTextMap.put(sig, example.text);
    }
  }

  public abstract String getSignature(BWImage image);

  public Tree toTree() {
    Tree tExamples = new Tree();
    for (String sig : sigToTextMap.keySet()) {
      List<String> list = sigToTextMap.get(sig);
      Tree tValues = new Tree();
      for (String value : list) tValues.addUnquotedString(value);
      tExamples.addUnquotedString(sig).add(tValues);
    }
    return OCRUtil.treeFor(this).add(tExamples);
  }

  public void fromTree(Tree tree) {
    Tree tExamples = tree.get(0);
    for (int i = 0; i < tExamples.namelessChildrenCount(); i += 2) {
      String sig = tExamples.getUnquotedString(i);
      Tree tValues = tExamples.get(i+1);
      for (Tree t : tValues.namelessChildren())
        sigToTextMap.put(sig, t.getName());
    }
  }
}
