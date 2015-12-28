package eyedev._01;

public class RecognizedText {
  public final String text;
  public final float confidence;

  public RecognizedText(String text, float confidence) {
    this.text = text;
    this.confidence = confidence;
  }

  public RecognizedText(String text) {
    this.text = text;
    confidence = text == null ? 0f: 1f;
  }

  @Override
  public String toString() {
    return text + " (confidence=" + confidence + ")";
  }
}
