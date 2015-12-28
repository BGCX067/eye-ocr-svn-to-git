package eyedev._08;

public class Range {
  float min = Float.NaN, max = Float.NaN;

  public Range() {
  }

  public Range(float min, float max) {
    this.max = max;
    this.min = min;
  }

  public void add(float example) {
    if (Double.isNaN(min) || example < min)
      min = example;
    if (Double.isNaN(max) || example > max)
      max = example;
  }

  public String toString() {
    return "(" + min + ", " + max + ")";
  }

  public boolean intersects(Range range) {
    double a = Math.max(min, range.min);
    double b = Math.min(max, range.max);
    return a <= b;
  }
}