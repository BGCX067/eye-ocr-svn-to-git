package eyedev._08;

public class TLShortest extends TopList {
  public void add(String item, float score) {
    if (list.size() == 0)
      super.add(item, score);
    else {
      if (isBetterThan(item, get(0)))
        list.set(0, new ScoredRecognizer(item, score));
    }
  }

  private boolean isBetterThan(String item1, String item2) {
    return item1.length() < item2.length();
  }
}
