package eyedev._01;

import prophecy.common.image.BWImage;

import java.awt.*;

public class ImageWithMarkLines {
  public BWImage image;
  public int topLine, baseLine;

  public ImageWithMarkLines(BWImage image) {
    this.image = image;
  }

  public ImageWithMarkLines(BWImage image, int topLine, int baseLine) {
    this.image = image;
    this.topLine = topLine;
    this.baseLine = baseLine;
  }

  public ImageWithMarkLines trim() {
    Rectangle r = OCRImageUtil.getBoundingBox(image);
    ImageWithMarkLines imageWithMarkLines = new ImageWithMarkLines(image.clip(r));
    if (topLine != 0 || baseLine != 0) {
      imageWithMarkLines.topLine = topLine-r.y;
      imageWithMarkLines.baseLine = baseLine-r.y;
    }
    return imageWithMarkLines;
  }
}
