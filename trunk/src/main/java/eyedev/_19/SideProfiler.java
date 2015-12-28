package eyedev._19;

import eyedev._01.ExtendedImageReader;
import eyedev._01.InputImage;
import eyedev._01.RecognizedText;
import prophecy.common.image.BWImage;

public class SideProfiler extends ExtendedImageReader {
  private BWImage image;
  private int w, h;
  private float threshold = 0.5f;

  public RecognizedText extendedReadImage(InputImage image) {
    this.image = image.image;
    w = image.image.getWidth();
    h = image.image.getHeight();
    /*System.out.println("topLine: " + image.topLine + ", baseLine: " + image.baseLine
      + ", height: " + image.image.getHeight());*/

    String signature = new SideProfileMaker(this.image, 0).getSignature();
    System.out.println("signature: " + signature);
    return new RecognizedText(null);
  }
}
