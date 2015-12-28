package eyedev._01;

import drjava.util.StringUtil;
import prophecy.common.image.BWImage;

import java.util.ArrayList;
import java.util.List;

public class ExampleSet {
  public List<Example> examples = new ArrayList<Example>();

  public ExampleSet(Example... examples) {
    for (Example example : examples)
      add(example);
  }

  public void add(Example example) {
    examples.add(example);
  }

  public void add(BWImage image, String text) {
    add(new Example(image, text));
  }

  @Override
  public String toString() {
    return StringUtil.n(examples.size(), "example");
  }

  public boolean verify(ImageReader recognizer) {
    for (Example example : examples) {
      if (!example.verify(recognizer))
        return false;
    }
    return true;
  }

  public int size() {
    return examples.size();
  }

  public void add(ExampleSet exampleSet) {
    examples.addAll(exampleSet.examples);
  }

  public void crop() {
    for (Example example : examples) {
      example.crop();
    }
  }

  public Example get(int row) {
    return examples.get(row);
  }
}
