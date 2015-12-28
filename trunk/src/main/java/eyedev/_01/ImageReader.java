package eyedev._01;

import prophecy.common.image.BWImage;

public abstract class ImageReader extends Processor {
  // subclasses override either readImage or extendedReadImage
  public abstract String readImage(BWImage image);

  // subclasses override either readImage or extendedReadImage
  public RecognizedText extendedReadImage(InputImage image) {
    return new RecognizedText(readImage(image.image));
  }

  protected String delegateRecognition(ImageReader imageReader, BWImage image) {
    prepareDelegation(imageReader);
    String result = imageReader.readImage(image);
    postprocessDelegation(imageReader);
    return result;
  }

  protected void postprocessDelegation(ImageReader imageReader) {
    if (collectDebugInfo)
      debugInfo = imageReader.getDebugInfo();
  }

  protected void prepareDelegation(ImageReader imageReader) {
    imageReader.setCollectDebugInfo(collectDebugInfo);
    imageReader.setStatusListener(statusListener);
  }

  public CharacterLearner getCharacterLearner() {
    return null;
  }
}
