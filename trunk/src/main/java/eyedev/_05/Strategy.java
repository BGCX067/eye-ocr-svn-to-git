package eyedev._05;

import eyedev._01.ExampleSet;
import eyedev._02.Streamable;

public abstract class Strategy implements Streamable {
  public ExampleSet exampleSet;

  public void setExampleSet(ExampleSet exampleSet) {
    this.exampleSet = exampleSet;
  }
}
