package eye.eye04;

import drjava.util.Errors;
import eye.eye02.FontEntry;
import eyedev._01.ExampleSet;
import eyedev._07.MultipleSizesRecognitionTest;
import eyedev._07.RecognitionTest;
import eyedev._07.SingleFontRecognitionTest;
import eyedev._07.StringsMaker;
import eyedev._08.SimpleExperiment;
import eyedev._08.TLBestScore;

import java.awt.*;
import java.util.List;

public class IndividualLettersChallenge extends Challenge {
  private FontEntry fontEntry;
  private List<Float> sizes;
  private Font font;
  private boolean error;

  public IndividualLettersChallenge(FontEntry fontEntry, List<Float> sizes) {
    this.fontEntry = fontEntry;
    this.sizes = sizes;
    name = "Recognize individual letters";
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
    StringsMaker strings = new SingleChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
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

  public List<Float> getSizes() {
    return sizes;
  }
}
