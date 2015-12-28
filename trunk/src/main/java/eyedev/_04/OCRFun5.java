package eyedev._04;

import eyedev._01.CT_ExampleSet;
import eyedev._01.ExampleSet;
import eyedev._01.PhotographicMemory;
import eyedev._03.RandomPlacement;

public class OCRFun5 {
  public static void main(String[] args) {
    ExampleSet baseExampleSet = new CT_ExampleSet();
    ExampleSet exampleSet = RandomPlacement.transform(10, 10, baseExampleSet);

    Experiment_v2 experiment = new Experiment_v2(exampleSet);
    experiment.addImageReader(new PhotographicMemory(exampleSet));
    experiment.addSimplifier(new Crop());
    experiment.setExperimentMaker(new ExperimentMaker() {
      public Experiment_v2 makeExperiment(ExampleSet exampleSet) {
        Experiment_v2 experiment = new Experiment_v2(exampleSet);
        experiment.addImageReader(new PhotographicMemory(exampleSet));
        return experiment;
      }
    });
    experiment.run();
  }
}
