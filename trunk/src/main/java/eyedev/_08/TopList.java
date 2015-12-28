package eyedev._08;

import drjava.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TopList {
  public List<ScoredRecognizer> list = new ArrayList<ScoredRecognizer>();

  public void add(String item, float score) {
    list.add(new ScoredRecognizer(item, score));
  }

  public int size() {
    return list.size();
  }

  public String get(int index) {
    return list.get(index).recognizer;
  }

  public float getScore(int index) {
    return list.get(index).score;
  }

  public void print() {
    System.out.println("Toplist: " + StringUtil.n(size(), "entry", "entries"));
    for (ScoredRecognizer item : list) {
      System.out.println("  (score: " + formatScore(item.score) + ") "
        + "[" + item.recognizer.length() + "] " + item.recognizer);
    }
    System.out.println();
  }

  private String formatScore(float score) {
    return StringUtil.formatDouble(score, 2);
  }

  public float getTopScore() {
    return size() == 0 ? 0f : getScore(0);
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public String getTopEntry() {
    return isEmpty() ? null : get(0);
  }
}
