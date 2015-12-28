package eyedev._07;

import prophecy.common.image.RGBImage;

public abstract class ProtocolEntry {
  public String correctText, recognizedText;

  protected ProtocolEntry(String correctText, String recognizedText) {
    this.correctText = correctText;
    this.recognizedText = recognizedText;
  }

  public abstract RGBImage getImage();
}
