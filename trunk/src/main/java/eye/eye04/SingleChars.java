package eye.eye04;

import eyedev._07.StringsMaker;

import java.util.ArrayList;
import java.util.List;

public class SingleChars implements StringsMaker {
  private String alphabet;

  public SingleChars(String alphabet) {
    this.alphabet = alphabet;
  }

  public List<String> makeStrings() {
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < alphabet.length(); i++)
      list.add(alphabet.substring(i, i+1));
    return list;
  }
}
