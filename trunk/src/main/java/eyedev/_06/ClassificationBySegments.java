package eyedev._06;

import drjava.util.MultiMap;
import drjava.util.StringUtil;
import eyedev._01.Example;
import eyedev._01.ExampleSet;
import eyedev._05.SegmentCounter;

import java.util.List;

public class ClassificationBySegments {
  MultiMap<String, String> multiMap = new MultiMap<String, String>();

  public ClassificationBySegments(ExampleSet exampleSet) {
    for (Example example : exampleSet.examples) {
      String s = SegmentCounter.getXYSegmentSignature(example.image);
      multiMap.put(s, example.text);
    }

    for (String s : multiMap.keySet()) {
      List<String> letters = multiMap.get(s);
      System.out.println(StringUtil.join(" ", letters) + " : " + s);
    }
  }
}
