package eye.eye04;

import drjava.util.Errors;
import drjava.util.RepeatableRandomizer;
import eye.eye02.FontEntry;
import eyedev._01.ExampleSet;
import eyedev._07.MultipleSizesRecognitionTest;
import eyedev._07.RandomStrings;
import eyedev._07.RecognitionTest;
import eyedev._08.SimpleExperiment;
import eyedev._08.TLBestScore;

import java.awt.*;
import java.util.List;

public class RandomWordsChallenge extends Challenge {
  private FontEntry fontEntry;
  private java.util.List<Float> sizes;
  private Font font;
  private boolean error;
  private int numExamples, wordLength;

  public RandomWordsChallenge(FontEntry fontEntry, int numExamples, int wordLength, List<Float> sizes) {
    this.fontEntry = fontEntry;
    this.numExamples = numExamples;
    this.wordLength = wordLength;
    this.sizes = sizes;
    name = "Recognize words (" + numExamples + "*" + wordLength + " chars)";
    charset = "A-Z";
    fontName = fontEntry.getName();
  }

  public float evaluate(String solution) {
    if (error) return 0;
    if (getFont() == null) return 0;

    RecognitionTest test = getTest();
    ExampleSet exampleSet = test.getExampleSet();
    
    TLBestScore toplist = new TLBestScore();
    SimpleExperiment experiment = new SimpleExperiment(exampleSet);
    experiment.addTopList(toplist);
    experiment.tryRecognizer(solution);
    return toplist.getTopScore() * 100;
  }

  @Override
  public RecognitionTest getTest() {
    RandomStrings strings = new RandomStrings(numExamples, wordLength, "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
      new RepeatableRandomizer());
    return new MultipleSizesRecognitionTest("", font, strings, sizes);
  }

  public Font getFont() {
    if (font != null) return font;
    try {
      font = fontEntry.loadFont(30f);
    } catch (Throwable e) {
      error = true;
      Errors.report(e);
    }
    return font;
  }
}
