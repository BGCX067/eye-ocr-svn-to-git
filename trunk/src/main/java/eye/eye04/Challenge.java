package eye.eye04;

import eyedev._07.RecognitionTest;
import prophecy.common.Var;

public abstract class Challenge {
  public String name;
  public String charset;
  public String fontName;
  private Var<Float> percentSolved = new Var<Float>(0f);
  private String solution;

  public abstract float evaluate(String solution);

  public boolean trySolution(String solution) {
    float score = evaluate(solution);
    if (score > getPercentSolved()) {
      percentSolved.set(score);
      this.solution = solution;
      return true;
    }
    return false;
  }

  public float getPercentSolved() {
    return percentSolved.get();
  }

  public String getSolution() {
    return solution;
  }

  public abstract RecognitionTest getTest();
}
