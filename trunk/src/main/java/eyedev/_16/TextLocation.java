package eyedev._16;

import eyedev._09.Subrecognition;

public class TextLocation {
  public int charIndex1, charIndex2;
  public Subrecognition subrecognition;

  public TextLocation(int charIndex1, int charIndex2, Subrecognition subrecognition) {
    this.charIndex1 = charIndex1;
    this.charIndex2 = charIndex2;
    this.subrecognition = subrecognition;
  }
}
