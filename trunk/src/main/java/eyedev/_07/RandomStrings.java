package eyedev._07;

import drjava.util.Randomizer;

import java.util.ArrayList;
import java.util.List;

public class RandomStrings implements StringsMaker {
  private int rows, cols;
  private String alphabet;
  private Randomizer randomizer;

  public RandomStrings(int rows, int cols, String alphabet, Randomizer randomizer) {
    this.rows = rows;
    this.cols = cols;
    this.alphabet = alphabet;
    this.randomizer = randomizer;
  }

  public List<String> makeStrings() {
    List<String> list = new ArrayList<String>(rows);
    for (int row = 0; row < rows; row++)
      list.add(makeString());
    return list;
  }

  private String makeString() {
    char[] chars = new char[cols];
    for (int col = 0; col < cols; col++)
      chars[col] = alphabet.charAt(randomizer.random(alphabet.length()));
    return new String(chars);
  }
}
