package eyedev._08;

import eyedev._01.Example;
import eyedev._01.ExampleSet;
import eyedev._01.ImageReader;

public class Scorer {
  public static float getRelativeScore(ImageReader recognizer, ExampleSet exampleSet) {
    int score = 0;
    for (Example example : exampleSet.examples) {
      if (example.verify(recognizer))
        ++score;
    }
    return (float) score/exampleSet.size();
  }
}
