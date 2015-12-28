package eyedev._07;

import eyedev._01.Example;
import eyedev._01.ExampleSet;

public abstract class RecognitionTest {
  protected String name;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public abstract int getRowCount();
  public abstract Example getRow(int row);

  public ExampleSet getExampleSet() {
    ExampleSet exampleSet = new ExampleSet();
    for (int i = 0; i < getRowCount(); i++)
      exampleSet.add(getRow(i));
    return exampleSet;
  }
}
