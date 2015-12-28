package eyedev._09;

import eyedev._01.RecognizedText;
import prophecy.common.image.BWImage;

import java.awt.*;

/** This is typically a recognition on the character-level */
public class Subrecognition implements Translatable, Cloneable {
  public Rectangle clip;
  public BWImage image;
  public int topLine, baseLine;
  public String recognizer;
  public String text;
  public float confidence;
  public RecognizedLine line;

  public Subrecognition(Rectangle clip, BWImage image, String recognizer, String text) {
    this.clip = clip;
    this.image = image;
    this.recognizer = recognizer;
    this.text = text;
    confidence = text == null ? 0f : 1f;
  }

  public Subrecognition(Rectangle clip, BWImage image, String recognizer, RecognizedText text) {
    this.clip = clip;
    this.image = image;
    this.recognizer = recognizer;
    if (text != null) {
      this.text = text.text;
      confidence = text.confidence;
    }
  }

  public Subrecognition translate(int x, int y) {
    Rectangle r = new Rectangle(clip);
    r.translate(x, y);
    try {
      Subrecognition s = (Subrecognition) clone();
      s.clip = r;
      return s;
    } catch (CloneNotSupportedException e) { // doesn't happen anyway
      throw new RuntimeException(e);
    }
    //return new Subrecognition(r, image, recognizer, new RecognizedText(text, confidence));
  }
}
