package eyedev._20;

import eyedev._08.FontLearner;
import eyedev._13.CompareImages;

public class CompareImagesLearner extends FontLearner {
  public void go() {
    makeExamples();
    makeExperiment();
    experiment.tryRecognizer(new CompareImages(exampleSet));
  }
}
