package eyedev._08;

import eyedev._01.ExampleSet;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;

import java.util.ArrayList;
import java.util.List;

public class SimpleExperiment {
  public ExampleSet exampleSet;
  public List<TopList> topLists = new ArrayList<TopList>();
  protected Runnable progressListener;

  public SimpleExperiment(ExampleSet exampleSet) {
    this.exampleSet = exampleSet;
  }

  public void tryRecognizer(String recognizerDesc) {
    tryRecognizer(OCRUtil.makeImageReader(recognizerDesc));
  }

  public void tryRecognizer(ImageReader recognizer) {
    if (isValid(recognizer)) {
      String desc = OCRUtil.getImageReaderDescription(recognizer);
      float score = Scorer.getRelativeScore(recognizer, exampleSet);
      boolean progress = false;
      for (TopList topList : topLists) {
        float oldScore = topList.getTopScore();
        topList.add(desc, score);
        if (topList.getTopScore() > oldScore)
          progress = true;
      }
      if (progress && progressListener != null)
        progressListener.run();
    }
  }

  private boolean isValid(ImageReader recognizer) {
    return true /*exampleSet.verify(recognizer)*/;
  }

  public void addTopList(TopList topList) {
    topLists.add(topList);
  }

  public void setProgressListener(Runnable progressListener) {
    this.progressListener = progressListener;
  }
}
