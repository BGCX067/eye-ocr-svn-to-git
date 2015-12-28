package eyedev._01;

import prophecy.common.image.BWImage;

public class Example {
  public BWImage image;
  public int topLine, baseLine;
  public String text;

  public Example(BWImage image, String text) {
    this.image = image;
    this.text = text;
  }

  public Example(ImageWithMarkLines image, String text) {
    this.image = image.image;
    topLine = image.topLine;
    baseLine = image.baseLine;
    this.text = text;
  }

  public boolean verify(ImageReader recognizer) {
    return text.equals(recognizer.readImage(image));
  }

  public void crop() {
    image = OCRImageUtil.trim(image);
  }
}
