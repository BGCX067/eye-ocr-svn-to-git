package eyedev._19;

import eyedev._08.FontLearner;

public class SideProfileLearner extends FontLearner {
  public void go() {
    makeExamples();
    makeExperiment();
    experiment.tryRecognizer(new SideProfileRecognizer(exampleSet));
  }
}
