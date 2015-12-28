package eyedev._01;

import prophecy.common.image.BWImage;

public abstract class ExtendedImageReader extends ImageReader {
  public String readImage(BWImage image) {
    RecognizedText recognizedText = extendedReadImage(new InputImage(image));
    return recognizedText == null ? null : recognizedText.text;
  }

  public RecognizedText delegateRecognition(ImageReader imageReader, InputImage inputImage) {
    prepareDelegation(imageReader);
    RecognizedText result = imageReader.extendedReadImage(inputImage);
    postprocessDelegation(imageReader);
    return result;
  }

  public abstract RecognizedText extendedReadImage(InputImage image);
}
