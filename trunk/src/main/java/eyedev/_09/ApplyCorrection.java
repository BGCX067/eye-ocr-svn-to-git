package eyedev._09;

import eyedev._01.*;
import eyedev._21.Correction;

import java.awt.*;

public class ApplyCorrection extends ExtendedImageReader {
  private ImageReader charRecognizer;

  public ApplyCorrection(ImageReader charRecognizer) {
    this.charRecognizer = charRecognizer;
  }

  public RecognizedText extendedReadImage(InputImage inputImage) {
    if (inputImage.corrections != null) {
      Rectangle r = new Rectangle(0, 0, inputImage.image.getWidth(), inputImage.image.getHeight());
      for (Correction correction : inputImage.corrections) {
        if (correction.getRectangle().equals(r))
          return new RecognizedText(correction.getText());
      }
    }
    return delegateRecognition(charRecognizer, inputImage);
  }
}
