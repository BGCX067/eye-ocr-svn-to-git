package eyedev._01;

import eyedev._21.Corrections;
import prophecy.common.image.BWImage;

public class InputImage {
  public BWImage image;
  public Integer topLine, baseLine;
  public Corrections corrections = new Corrections();

  public InputImage(BWImage image) {
    this.image = image;
  }
}
