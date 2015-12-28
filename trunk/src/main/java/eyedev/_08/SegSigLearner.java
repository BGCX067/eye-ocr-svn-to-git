package eyedev._08;

import drjava.util.StringUtil;
import eyedev._01.Example;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import eyedev._06.SegmentSignature;
import prophecy.common.image.BWImage;

import java.util.List;

public class SegSigLearner extends FontLearner {
  public SegSigLearner() {
  }

  @Override
  public void go() {
    makeExamples();
    makeExperiment();

    experiment.tryRecognizer(new SegmentSignature(exampleSet));

    float lastTopScore = toplist.getTopScore();
    while (lastTopScore < 1f && toplist.size() != 0 && !isCancelled()) {
      tryToFixRecognizer(toplist.get(0), experiment);
      float newScore = toplist.getTopScore();
      if (newScore == lastTopScore)
        break;
      setStatus("Accuracy improved to " + StringUtil.formatDouble(newScore*100, 1) + "%");
      lastTopScore = newScore;
    }

    toplist.print();
  }

  private void tryToFixRecognizer(String desc, SimpleExperiment experiment) {
    ImageReader recognizer = OCRUtil.makeImageReader(desc);
    for (Example example : experiment.exampleSet.examples) {
      String answer = recognizer.readImage(example.image);
      if (!example.text.equals(answer)) {
        //System.out.println("Recognizer fails on " + example.text + " (gives " + answer + ")");
        if (tryToFix(recognizer, example.text, answer))
          return;
      }
    }
  }

  private boolean tryToFix(ImageReader recognizer, String char1, String char2) {
    List<BWImage> images1 = exampleMap.get(char1);
    List<BWImage> images2 = exampleMap.get(char2);

    boolean fixFound = false;
    for (int featureNr = 0; featureNr < StandardFeatures.getNumberOfFeatures(); featureNr++) {
      Fix fix = FixMaker.makeFix(featureNr, images1, images2, char1, char2, recognizer);
      if (fix != null) {
        experiment.tryRecognizer(fix);
        fixFound = true;
      }
    }
    return fixFound;
  }
}
