package eyedev._03;

import eyedev._01.CT_ExampleSet;
import eyedev._01.ExampleSet;
import eyedev._01.PhotographicMemory;

public class OCRFun4 {
  public static void main(String[] args) {
    ExampleSet baseExampleSet = new CT_ExampleSet();
    ExampleSet exampleSet = RandomPlacement.transform(10, 10, baseExampleSet);

    Experiment experiment = new Experiment(exampleSet);
    experiment.addImageReader(new PhotographicMemory(exampleSet));
    experiment.run();
  }
}
