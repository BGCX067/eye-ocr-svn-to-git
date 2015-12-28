package eyedev._12;

import eyedev._01.Example;
import eyedev._01.ExampleSet;
import eyedev._07.RecognitionTest;

public class ExampleSetRecognitionTest extends RecognitionTest {
  private ExampleSet exampleSet;

  public ExampleSetRecognitionTest(ExampleSet exampleSet) {
    this.exampleSet = exampleSet;
  }

  @Override
  public int getRowCount() {
    return exampleSet.size();
  }

  @Override
  public Example getRow(int row) {
    return exampleSet.get(row);
  }
}
