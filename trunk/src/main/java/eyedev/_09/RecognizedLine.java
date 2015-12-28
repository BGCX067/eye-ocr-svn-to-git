package eyedev._09;

import java.awt.*;

public class RecognizedLine {
  public Rectangle boundingBox;
  public String text;

  public RecognizedLine(Rectangle boundingBox, String text) {
    this.boundingBox = boundingBox;
    this.text = text;
  }
}
