package eyedev._08;

public class TLBestScore extends TopList {
  public void add(String item, float score) {
    if (list.size() == 0)
      super.add(item, score);
    else {
      if (score > getScore(0))
        list.set(0, new ScoredRecognizer(item, score));
    }
  }
}
