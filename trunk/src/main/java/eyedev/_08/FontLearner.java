package eyedev._08;

import drjava.util.MultiMap;
import eyedev._01.Example;
import eyedev._01.ExampleSet;
import eyedev._01.StatusListener;
import eyedev._05.Alphabet;
import prophecy.common.image.BWImage;

import java.awt.*;
import java.util.*;

public abstract class FontLearner {
  protected TopList toplist = new TLBestScore();
  protected java.util.List<Font> fonts = new ArrayList<Font>();
  protected boolean allChars;
  protected Runnable progressListener;
  protected StatusListener statusListener;
  protected ExampleSet exampleSet;
  protected MultiMap<String, BWImage> exampleMap;
  protected SimpleExperiment experiment;

  public void addFont(Font font) {
    fonts.add(font);
  }

  public abstract void go();

  protected boolean isCancelled() {
    return statusListener != null && statusListener.processCancelled();
  }

  protected void setStatus(String status) {
    if (statusListener != null)
      statusListener.setStatus(status);
    else
      System.out.println(status);
  }

  public TopList getToplist() {
    return toplist;
  }

  public void setAllChars(boolean allChars) {
    this.allChars = allChars;
  }

  public String getBestRecognizer() {
    return toplist.getTopEntry();
  }

  public void setProgressListener(Runnable runnable) {
    progressListener = runnable;
  }

  public void setStatusListener(StatusListener statusListener) {
    this.statusListener = statusListener;
  }

  protected void makeExamples() {
    exampleSet = new ExampleSet();
    for (Font font : fonts) {
      if (allChars)
        exampleSet.add(Alphabet.makeAllChars(font));
      else
        exampleSet.add(Alphabet.makeAlphabet(font));
    }

    exampleSet.crop();

    // make exampleMap

    exampleMap = new MultiMap<String, BWImage>();
    for (Example example : exampleSet.examples) {
      exampleMap.put(example.text, example.image);
    }
  }

  protected void makeExperiment() {
    experiment = new SimpleExperiment(exampleSet);
    experiment.setProgressListener(progressListener);
    experiment.addTopList(toplist);
  }
}
